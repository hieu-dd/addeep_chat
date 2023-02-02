package net.itanchi.addeep.server.rest.user.response

import kotlinx.serialization.Serializable
import net.itanchi.addeep.server.rest.Response
import net.itanchi.addeep.server.rest.user.dto.UserDTO

@Serializable
data class UserResponse(
    override val code: Int = 0,
    override val message: String = "success",
    override val data: UserDTO? = null
) : Response<UserDTO>()