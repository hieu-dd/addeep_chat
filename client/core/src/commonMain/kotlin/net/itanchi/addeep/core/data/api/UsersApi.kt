package net.itanchi.addeep.core.data.api

import kotlinx.coroutines.flow.Flow
import net.itanchi.addeep.core.data.api.request.*
import net.itanchi.addeep.core.data.api.response.*

internal interface UsersApi {
    suspend fun getUser(request: GetUserRequest): GetUserResponse
    suspend fun updateUser(request: UpdateUserRequest): UpdateUserResponse
    suspend fun uploadUserAvatar(request: UploadRequest): Flow<Pair<Long, Long>>
    suspend fun syncUserContacts(request: SyncUserContactsRequest): SyncUserContactsResponse
    suspend fun updatePushToken(request: UpdatePushTokenRequest): UpdatePushTokenResponse
    suspend fun addContact(request: AddContactRequest): AddContactResponse
}