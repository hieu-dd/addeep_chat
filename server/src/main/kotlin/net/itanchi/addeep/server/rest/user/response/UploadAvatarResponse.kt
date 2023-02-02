package net.itanchi.addeep.server.rest.user.response

import kotlinx.serialization.Serializable
import net.itanchi.addeep.server.rest.Response

@Serializable
data class UploadAvatarResponse(
    override val message: String = "success",
    override val code: Int = 0,
    override val data: String
) : Response<String>()