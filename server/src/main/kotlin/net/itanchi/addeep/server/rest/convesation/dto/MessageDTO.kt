package net.itanchi.addeep.server.rest.convesation.dto

import kotlinx.serialization.Serializable
import net.itanchi.addeep.server.repository.message.MessageType
import net.itanchi.addeep.server.rest.user.dto.UserDTO
import net.itanchi.addeep.server.service.conversation.Attachment
import net.itanchi.addeep.server.service.conversation.Message
import net.itanchi.addeep.server.utils.serializers.InstantSerializer
import java.time.Instant

@Serializable
data class MessageDTO(
    val id: Long,
    val message: String,
    val sender: UserDTO,
    val type: MessageType,
    val attachments: List<AttachmentDTO> = listOf(),
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant,
    @Serializable(with = InstantSerializer::class)
    val updatedAt: Instant,
) {
    companion object {
        fun fromMessage(message: Message): MessageDTO = MessageDTO(
            id = message.id,
            message = message.message,
            type = message.type,
            sender = UserDTO.fromUser(
                user = message.sender
            ),
            createdAt = message.createdAt,
            updatedAt = message.updatedAt,
            attachments = message.attachments.map { AttachmentDTO.fromAttachment(it) }
        )
    }
}

@Serializable
data class AttachmentDTO(
    val id: Long = 0,
    val type: String,
    val originalName: String,
    val name: String,
    val size: Long,
    val md5: String,
) {
    companion object {
        fun fromAttachment(attachment: Attachment) = with(attachment) {
            AttachmentDTO(
                id = id,
                type = type,
                originalName = originalName,
                name = name,
                size = size,
                md5 = md5
            )
        }
    }
}