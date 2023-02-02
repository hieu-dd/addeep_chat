package net.itanchi.addeep.core.data.api.response

import kotlinx.serialization.Serializable
import net.itanchi.addeep.core.data.model.Contact
import net.itanchi.addeep.core.data.model.User

@Serializable
internal data class SyncUserContactsResponse(
    override val code: Int,
    override val message: String,
    override val data: List<User> = listOf(),
) : Response<List<User>>()