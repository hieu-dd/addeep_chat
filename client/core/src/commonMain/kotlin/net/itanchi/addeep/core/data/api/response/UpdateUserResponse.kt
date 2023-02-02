package net.itanchi.addeep.core.data.api.response

import kotlinx.serialization.Serializable
import net.itanchi.addeep.core.data.model.User

@Serializable
internal data class UpdateUserResponse(
    override val code: Int,
    override val message: String,
    override val data: User? = null,
) : Response<User>()