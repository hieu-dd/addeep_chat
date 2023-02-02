package net.itanchi.addeep.server.rest.media.dto

import kotlinx.serialization.Serializable

@Serializable
data class GifDTO(
    val url: String,
    val dataSource: String,
)