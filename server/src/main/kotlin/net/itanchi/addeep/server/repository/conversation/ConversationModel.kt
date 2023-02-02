package net.itanchi.addeep.server.repository.conversation

import net.itanchi.addeep.server.repository.message.MessageModel
import net.itanchi.addeep.server.repository.user.UserModel
import net.itanchi.addeep.server.repository.user.toUser
import net.itanchi.addeep.server.service.conversation.Conversation
import net.itanchi.addeep.server.service.conversation.Message
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("conversations")
data class ConversationModel(
    @Id
    @Column("id")
    var id: Long = 0,

    @Column("title")
    var title: String,

    @Column("type")
    var type: ConversationType,

    @Column("creator_id")
    var creatorId: Long,

    @Column("created_at")
    @CreatedDate
    var createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    var updatedAt: Instant = Instant.now(),
)

internal fun ConversationModel.toConversation(
    participants: List<UserModel>,
    messages: List<MessageModel>,
    creator: UserModel,
    senderInfos: List<UserModel>
): Conversation = Conversation(
    id = id,
    title = title,
    type = type,
    participants = participants.map { it.toUser() },
    messages = messages.map { message ->
        Message(
            id = message.id,
            message = message.message,
            conversationId = 0,
            type = message.messageType,
            sender = senderInfos.first { it.id == message.senderId }.toUser(),
            createdAt = message.createdAt,
            updatedAt = message.updatedAt,
            attachments = message.attachments.map { it.toAttachment() }
        )
    },
    creator = creator.toUser()
)

enum class ConversationType {
    Single,
    Group
}