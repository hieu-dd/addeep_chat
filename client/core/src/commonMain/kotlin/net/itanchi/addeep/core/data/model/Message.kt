package net.itanchi.addeep.core.data.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.itanchi.addeep.core.util.randomUUID

@Serializable
data class Message(
    val id: Long = 0,
    val localId: String = randomUUID(),
    val sender: User = User(),
    val message: String,
    var stickerUrl: String = "",
    val attachments: List<MessageAttachment> = listOf(),
    val type: MessageType = MessageType.Undefined,
    val status: MessageStatus = MessageStatus.Sending,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
) {

    fun getMessageUrl(conversationId: Long) = "http://localhost:8080/api/v1/conversations/$conversationId/messages/$id"

}

@Serializable
data class MessageAttachment(
    val id: Long = 0,
    val name: String,
    val originalName: String,
    val type: String,
    val size: Long = 0,
    var url: String = "",
    var localPath: String = "",
    @Transient
    val contents: ByteArray = ByteArray(0),
)

@Serializable
enum class MessageStatus {
    Sending,
    Sent,
    Viewed,
    Failed,
}

@Serializable
enum class MessageType {
    PlainText,
    Sticker,
    Gif,
    Photo,
    Document,
    Undefined,
}
