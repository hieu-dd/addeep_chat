package net.itanchi.addeep.server.service.gif

import kotlinx.serialization.Serializable

@Serializable
data class GiphyGif(
    val images: Images,
) {
    @Serializable
    data class Images(
        val original: Original,
    ) {
        @Serializable
        data class Original(
            val webp: String,
        )
    }
}

