package net.itanchi.addeep.core.data.api.response

import kotlinx.serialization.Serializable

@Serializable
internal data class LoginResponse(
    override val code: Int,
    override val message: String,
    override val data: String? = null,
) : Response<String>()