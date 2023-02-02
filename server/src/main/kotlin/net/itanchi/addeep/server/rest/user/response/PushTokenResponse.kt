package net.itanchi.addeep.server.rest.user.response

import kotlinx.serialization.Serializable
import net.itanchi.addeep.server.rest.Response

@Serializable
data class PushTokenResponse(
    override val code: Int = 0,
    override val message: String = "success"
) : Response<Nothing>()