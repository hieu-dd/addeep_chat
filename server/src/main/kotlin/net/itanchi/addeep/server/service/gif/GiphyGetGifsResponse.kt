package net.itanchi.addeep.server.service.gif

import kotlinx.serialization.Serializable

@Serializable
data class GiphyGetGifsResponse(
    val data: List<GiphyGif>
)