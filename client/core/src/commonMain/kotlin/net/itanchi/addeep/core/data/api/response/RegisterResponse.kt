package net.itanchi.addeep.core.data.api.response

import kotlinx.serialization.Serializable

@Serializable
internal data class RegisterResponse(
    override val code: Int,
    override val message: String,
) : Response<Nothing>()