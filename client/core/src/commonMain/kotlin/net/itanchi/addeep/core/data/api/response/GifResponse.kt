package net.itanchi.addeep.core.data.api.response

import kotlinx.serialization.Serializable
import net.itanchi.addeep.core.data.model.Gif

@Serializable
internal data class GifResponse(
    override val code: Int,
    override val message: String,
    override val data: List<Gif>? = null,
) : Response<List<Gif>>()