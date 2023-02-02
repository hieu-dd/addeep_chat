package net.itanchi.addeep.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.itanchi.addeep.core.util.unicode

@Serializable
data class Emoji(
    val name: String,
    val unified: String,
    val category: String,
    @SerialName("sort_order")
    val sortOrder: Int,
) {
    var message = unified.unicode()
}

@Serializable
data class EmojiCategory(
    val names: List<String>,
    val unified: String,
) {
    var message = unified.unicode()
}