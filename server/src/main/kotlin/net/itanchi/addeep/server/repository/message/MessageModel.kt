package net.itanchi.addeep.server.repository.message

import net.itanchi.addeep.server.repository.conversation.AttachmentModel
import net.itanchi.addeep.server.repository.conversation.toAttachment
import net.itanchi.addeep.server.repository.user.UserModel
import net.itanchi.addeep.server.service.conversation.Message
import net.itanchi.addeep.server.service.user.User
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("messages")
data class MessageModel(
    @Id
    @Column("id")
    var id: Long = 0,

    @Column("conversation_id")
    var conversationId: Long,

    @Column("sender_id")
    var senderId: Long,

    @Column("message_type")
    var messageType: MessageType,

    @Column("message")
    var message: String,

    @Column("created_at")
    @CreatedDate
    var createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    var updatedAt: Instant = Instant.now(),
) {
    @Transient
    var attachments: List<AttachmentModel> = listOf()
}

internal fun MessageModel.toMessage(senderInfo: UserModel) = Message(
    id = id,
    conversationId = conversationId,
    type = messageType,
    message = message,
    createdAt = createdAt,
    updatedAt = updatedAt,
    sender = User(
        id = senderId,
        name = senderInfo.name.orEmpty(),
        phone = senderInfo.getPhone(),
        email = senderInfo.email
    ),
    attachments = attachments.map { it.toAttachment() }
)

enum class MessageType {
    PlainText,
    Sticker,
    Gif,
    Photo,
    Document
}