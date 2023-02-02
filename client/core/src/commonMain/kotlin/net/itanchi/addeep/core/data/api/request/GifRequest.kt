package net.itanchi.addeep.core.data.api.request

import kotlinx.serialization.Serializable

@Serializable
internal data class GifRequest(
    val filter: String,
    val page: Int = 1,
    val pageSize: Int = 10,
)