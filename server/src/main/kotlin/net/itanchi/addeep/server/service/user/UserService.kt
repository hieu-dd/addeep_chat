package net.itanchi.addeep.server.service.user

import com.google.i18n.phonenumbers.NumberParseException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.itanchi.addeep.server.exception.Error
import net.itanchi.addeep.server.repository.contact.ContactRepo
import net.itanchi.addeep.server.repository.user.Gender
import net.itanchi.addeep.server.repository.user.UserModel
import net.itanchi.addeep.server.repository.user.UserRepo
import net.itanchi.addeep.server.repository.user.toUser
import net.itanchi.addeep.server.rest.user.request.Preferences
import net.itanchi.addeep.server.service.storage.GoogleStorageService.Companion.DEFAULT_BUCKET_NAME
import net.itanchi.addeep.server.service.storage.ResourceInfo
import net.itanchi.addeep.server.service.storage.StorageService
import net.itanchi.addeep.server.utils.PhoneNumberUtils
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class UserService(
    private val userRepo: UserRepo,
    private val contactRepo: ContactRepo,
    private val storageService: StorageService,
) {

    companion object {
        const val DEFAULT_AVATAR_CONTENT_TYPE = "image/png"
        const val DEFAULT_AVATAR_FORMAT = "png"
        const val MAX_AVATAR_SIZE_IN_MB = 1L
    }

    suspend fun findByIdOrPhoneOrAddeepId(
        userId: Long,
        phone: String?,
        addeepId: String?,
    ): User? = coroutineScope {
        userRepo.findByIdOrPhoneOrAddeepId(
            userId = userId,
            phone = phone.orEmpty(),
            addeepId = addeepId.orEmpty(),
        )?.toUser()
    }

    suspend fun getOrCreateUser(
        firebaseUid: String,
        name: String,
        phone: String,
        email: String?,
    ): User {
        return try {
            (userRepo.findByFirebaseUid(firebaseUid = firebaseUid) ?: let {
                val phoneNumber = phone.let { PhoneNumberUtils.parse(it) }
                userRepo.save(
                    UserModel(
                        firebaseUid = firebaseUid,
                        name = name,
                        countryCode = phoneNumber.countryCode.toString(),
                        phoneNumber = phoneNumber.nationalNumber.toString(),
                        email = email,
                        isActive = true,
                    )
                )
            }).toUser()
        } catch (exception: NumberParseException) {
            throw Error.ValidationDataError.InvalidTelephone
        }
    }

    suspend fun updateUser(
        userId: Long,
        addeepId: String? = null,
        allowToSearchByAddeepId: Boolean? = null,
        name: String? = null,
        gender: Gender? = null,
        dob: LocalDate? = null,
        avatar: String? = null,
        email: String? = null,
        preferences: Preferences? = null
    ): User {
        return userRepo.findById(userId)!!.apply {
            allowToSearchByAddeepId?.let {
                this@apply.allowToSearchByAddeepId = it
            }
            addeepId?.takeIf { this@apply.addeepId == null }?.let {
                this@apply.addeepId = addeepId
            }
            name?.let { this@apply.name = name }
            gender?.let { this@apply.gender = gender }
            dob?.let { this@apply.dob = dob }
            avatar?.let { this@apply.avatar = avatar }
            email?.let { this@apply.email = email }
            preferences?.let { newPreferences ->
                val finalPreferences = this@apply.preferences.takeIf {
                    it.isNotBlank()
                }?.let {
                    Json.decodeFromString<Preferences>(it).apply {
                        newPreferences.collectAndUsePersonalInfo?.let {
                            collectAndUsePersonalInfo = it
                        }
                    }
                } ?: newPreferences
                this@apply.preferences = Json.encodeToString(finalPreferences)
            }
        }.also { userRepo.save(it) }.toUser()
    }

    suspend fun uploadAvatar(
        userId: Long,
        data: Flow<DataBuffer>,
        downloadUrl: String,
        contentSize: Long?,
        contentType: String?
    ) {
        val limitSize = MAX_AVATAR_SIZE_IN_MB * 1024 * 1024
        when {
            contentSize != null && contentSize > limitSize -> throw Error.UploadFileError.ExceedMaxSize
            contentType != DEFAULT_AVATAR_CONTENT_TYPE -> throw Error.UploadFileError.InvalidFile
            else -> storageService.uploadFile(
                bucketName = DEFAULT_BUCKET_NAME,
                path = "users/$userId/avatar.$DEFAULT_AVATAR_FORMAT",
                contentType = DEFAULT_AVATAR_CONTENT_TYPE,
                data = data.map { it.asByteBuffer() },
                limitSize = limitSize
            ).also {
                updateUser(
                    userId = userId,
                    avatar = downloadUrl
                )
            }
        }
    }

    suspend fun downloadAvatar(
        userId: Long,
    ): ResourceInfo {
        return storageService.getResource("users/$userId/avatar.$DEFAULT_AVATAR_FORMAT")
    }
}