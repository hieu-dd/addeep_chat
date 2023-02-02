package net.itanchi.addeep.core.data.api.response

import kotlinx.serialization.Serializable

@Serializable
internal data class AddContactResponse(
    override val code: Int,
    override val message: String,
    override val data: Long? = null,
) : Response<Long>()