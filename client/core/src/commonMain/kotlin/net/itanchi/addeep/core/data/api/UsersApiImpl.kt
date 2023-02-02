package net.itanchi.addeep.core.data.api

import co.touchlab.stately.ensureNeverFrozen
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import net.itanchi.addeep.core.data.api.request.*
import net.itanchi.addeep.core.data.api.response.*

internal class UsersApiImpl(
    private val client: HttpClient,
    private val mediaClient: HttpClient,
) : UsersApi {
    companion object {
        private const val API_PATH_USERS = "/api/v1/users"
        private const val API_PATH_USER_AVATAR = "/api/v1/users/avatar"
        private const val API_PATH_USER_CONTACTS = "/api/v1/users/contacts"
        private const val API_PATH_PUSH_TOKEN = "/api/v1/users/push-token"
        private const val API_PATH_ADD_CONTACT = "/api/v1/users/add-contact"
    }

    init {
        ensureNeverFrozen()
    }

    override suspend fun getUser(request: GetUserRequest): GetUserResponse {
        return client.get(API_PATH_USERS) {
            parameter("addeepId", request.addeepId)
            parameter("phone", request.phone)
        }
    }

    override suspend fun updateUser(request: UpdateUserRequest): UpdateUserResponse {
        return client.put(API_PATH_USERS) {
            body = request
        }
    }

    override suspend fun uploadUserAvatar(request: UploadRequest): Flow<Pair<Long, Long>> = channelFlow {
        mediaClient.submitFormWithBinaryData<HttpResponse>(
            url = API_PATH_USER_AVATAR,
            formData = formData {
                append("files", request.contents, Headers.build {
                    append(HttpHeaders.ContentType, request.contentType)
                    append(HttpHeaders.ContentDisposition, "filename=${request.name}")
                })
            }
        ) {
            onUpload { bytesSentTotal, contentLength ->
                send(Pair(bytesSentTotal, contentLength))
            }
        }
    }

    override suspend fun syncUserContacts(request: SyncUserContactsRequest): SyncUserContactsResponse {
        return client.post(API_PATH_USER_CONTACTS) {
            body = request
        }
    }

    override suspend fun updatePushToken(request: UpdatePushTokenRequest): UpdatePushTokenResponse {
        return client.put(API_PATH_PUSH_TOKEN) {
            body = request
        }
    }

    override suspend fun addContact(request: AddContactRequest): AddContactResponse {
        return client.post(API_PATH_ADD_CONTACT) {
            body = request
        }
    }
}