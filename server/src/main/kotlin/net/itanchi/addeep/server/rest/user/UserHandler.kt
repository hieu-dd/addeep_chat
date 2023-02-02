package net.itanchi.addeep.server.rest.user

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import net.itanchi.addeep.server.config.client.UserAgent
import net.itanchi.addeep.server.exception.Error
import net.itanchi.addeep.server.rest.CustomHeaders.DOWNLOAD_FILE_HEADER
import net.itanchi.addeep.server.rest.user.dto.UserDTO
import net.itanchi.addeep.server.rest.user.request.AddContactRequest
import net.itanchi.addeep.server.rest.user.request.PushTokenRequest
import net.itanchi.addeep.server.rest.user.request.SyncContactsRequest
import net.itanchi.addeep.server.rest.user.request.UpdateUserRequest
import net.itanchi.addeep.server.rest.user.response.*
import net.itanchi.addeep.server.service.contact.ContactService
import net.itanchi.addeep.server.service.notification.NotificationService
import net.itanchi.addeep.server.service.point.PointInfo
import net.itanchi.addeep.server.service.point.PointService
import net.itanchi.addeep.server.service.user.User
import net.itanchi.addeep.server.service.user.UserService
import net.itanchi.addeep.server.utils.converters.toDeviceType
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*


@Component
class UserHandler(
    private val userService: UserService,
    private val contactService: ContactService,
    private val pointService: PointService,
    private val notificationService: NotificationService,
) {
    companion object {
        const val DEFAULT_AVATAR_FILE_NAME = "avatar.png"
        const val USER_ID_QUERY_PARAM = "userId"
    }

    suspend fun userInfo(request: ServerRequest): ServerResponse = coroutineScope {
        val userId = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        val addeepId = request.queryParamOrNull("addeepId")
        val phone = request.queryParamOrNull("phone")
        val selfRequest = addeepId == null && phone == null
        val userInfo = awaitAll(
            async {
                userService.findByIdOrPhoneOrAddeepId(
                    userId = userId,
                    addeepId = addeepId,
                    phone = phone,
                ) ?: throw Error.UserNotFound
            },
            async { if (selfRequest) pointService.getPointInfo(userId) else PointInfo() }
        )
        val user = userInfo[0] as User
        val pointInfo = userInfo[1] as PointInfo
        ServerResponse.ok()
            .json()
            .bodyValueAndAwait(
                UserResponse(
                    data = user.let {
                        UserDTO.fromUser(
                            user = it,
                            pointInfo = pointInfo,
                            includePreferences = selfRequest,
                            isFriend = contactService.verifyFriend(userId, it)
                        )
                    }
                )
            )
    }

    suspend fun syncContacts(request: ServerRequest): ServerResponse {
        val userId = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        val syncContactRequest = request.awaitBody<SyncContactsRequest>()
        return ServerResponse.ok()
            .json()
            .bodyValueAndAwait(
                SyncContactsResponse(
                    data = contactService.saveUserContacts(
                        userId,
                        syncContactRequest.contacts.flatMap { it.toContacts() }
                    )
                        .map { UserDTO.fromUser(it) }
                )
            )
    }

    suspend fun pushToken(
        request: ServerRequest
    ): ServerResponse {
        val userId = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        val userAgent = UserAgent.parseString(request.headers().header(HttpHeaders.USER_AGENT).firstOrNull().orEmpty())
            ?: throw Error.InvalidUserAgent
        val pushTokenRequest = request.awaitBody<PushTokenRequest>()
        val deviceType = userAgent.type.toDeviceType() ?: throw Error.InvalidDeviceType
        notificationService.registerDeviceToken(
            userId = userId,
            deviceToken = pushTokenRequest.deviceToken,
            deviceType = deviceType
        )
        return ServerResponse.ok().bodyValueAndAwait(PushTokenResponse())
    }

    suspend fun updateUser(
        request: ServerRequest
    ): ServerResponse {
        val userId = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        val updateUserRequest = request.awaitBody<UpdateUserRequest>()
        val user = userService.updateUser(
            addeepId = updateUserRequest.addeepId,
            allowToSearchByAddeepId = updateUserRequest.allowToSearchByAddeepId,
            userId = userId,
            name = updateUserRequest.name,
            gender = updateUserRequest.gender,
            dob = updateUserRequest.dob,
            email = updateUserRequest.email,
            preferences = updateUserRequest.preferences
        )
        return ServerResponse.ok().bodyValueAndAwait(
            UserResponse(
                data = UserDTO.fromUser(
                    user = user,
                    pointInfo = PointInfo()
                )
            )
        )
    }

    suspend fun uploadAvatar(
        request: ServerRequest
    ): ServerResponse {
        val filePart =
            request.multipartData().mapNotNull { it["files"] as? List<FilePart> }.awaitFirstOrNull()?.firstOrNull()
                ?: throw Error.UploadFileError.InvalidFile
        val userId = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        val downloadUrl = "${request.uri()}?$USER_ID_QUERY_PARAM=$userId"
        userService.uploadAvatar(
            userId = userId,
            data = filePart.content().asFlow(),
            downloadUrl = downloadUrl,
            contentSize = request.headers().contentLengthOrNull(),
            contentType = filePart.headers().contentType?.toString()
        )
        return ServerResponse.ok().bodyValueAndAwait(UploadAvatarResponse(data = downloadUrl))
    }

    suspend fun downloadAvatar(
        request: ServerRequest
    ): ServerResponse {
        val userId = request.queryParamOrNull(USER_ID_QUERY_PARAM)?.toLongOrNull()
            ?: ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        val resourceInfo = userService.downloadAvatar(userId)
        return ServerResponse.ok()
            .header(HttpHeaders.CONTENT_TYPE, resourceInfo.contentType)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$DEFAULT_AVATAR_FILE_NAME")
            .header(DOWNLOAD_FILE_HEADER, DEFAULT_AVATAR_FILE_NAME)
            .bodyValueAndAwait(
                InputStreamResource(resourceInfo.data)
            )
    }

    suspend fun addContact(
        request: ServerRequest
    ): ServerResponse {
        val addContactRequest = request.awaitBodyOrNull<AddContactRequest>()
            ?: throw Error.ValidationDataError.FieldIsInvalid("userId")
        val userId = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        val user = contactService.addContact(
            userId = userId,
            contactUserId = addContactRequest.userId,
        ) ?: throw Error.InvalidUser
        return ServerResponse.ok()
            .bodyValueAndAwait(
                AddContactResponse(
                    data = user.id
                )
            )
    }
}