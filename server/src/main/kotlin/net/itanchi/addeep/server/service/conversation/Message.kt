package net.itanchi.addeep.server.service.conversation

import net.itanchi.addeep.server.repository.message.MessageType
import net.itanchi.addeep.server.service.user.User
import java.time.Instant

data class Message(
    val id: Long,
    val conversationId: Long,
    val message: String,
    val type: MessageType,
    val sender: User,
    val createdAt: Instant,
    val updatedAt: Instant,
    val attachments: List<Attachment> = listOf()
)

data class Attachment(
    val id: Long = 0,
    val type: String,
    val originalName: String,
    val name: String,
    val size: Long,
    val md5: String,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
)