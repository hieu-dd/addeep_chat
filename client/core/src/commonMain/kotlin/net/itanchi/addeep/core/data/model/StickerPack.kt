package net.itanchi.addeep.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StickerPack(
    val id: Long,
    val name: String,
    val publisher: String,
    @SerialName("tray_image_file")
    val trayImageFile: String,
    @SerialName("animated_sticker_pack")
    val animatedStickerPack: Boolean = false,
    val stickers: List<Sticker> = listOf(),
)

@Serializable
data class Sticker(
    @SerialName("image_file")
    val imageFile: String,
    val emojis: List<String>,
) {
    var message: String = ""
}