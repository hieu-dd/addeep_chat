package net.itanchi.addeep.server.rest.media.response

import kotlinx.serialization.Serializable
import net.itanchi.addeep.server.rest.Response
import net.itanchi.addeep.server.rest.media.dto.GifDTO

@Serializable
data class GetGifsResponse(
    override val code: Int = 0,
    override val message: String = "success",
    override val data: List<GifDTO>,
) : Response<List<GifDTO>>()
