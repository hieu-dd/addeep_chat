package net.itanchi.addeep.server.rest.convesation.dto

import kotlinx.serialization.Serializable
import net.itanchi.addeep.server.repository.conversation.ConversationType
import net.itanchi.addeep.server.rest.user.dto.UserDTO
import net.itanchi.addeep.server.service.conversation.Conversation
import net.itanchi.addeep.server.utils.serializers.InstantSerializer
import java.time.Instant

@Serializable
data class ConversationDTO(
    val id: Long = 0,
    val title: String,
    val type: ConversationType,
    val creator: UserDTO,
    var participants: List<UserDTO> = listOf(),
    val messages: List<MessageDTO> = listOf(),
    @Serializable(with = InstantSerializer::class)
    var createdAt: Instant = Instant.now(),
    @Serializable(with = InstantSerializer::class)
    var updatedAt: Instant = Instant.now(),
) {
    companion object {
        fun fromConversation(conversation: Conversation): ConversationDTO = with(conversation) {
            ConversationDTO(
                id = id,
                title = title,
                type = type,
                creator = UserDTO.fromUser(creator),
                participants = participants.map { UserDTO.fromUser(it) },
                messages = messages.map{ MessageDTO.fromMessage(it) }
            )
        }
    }
}