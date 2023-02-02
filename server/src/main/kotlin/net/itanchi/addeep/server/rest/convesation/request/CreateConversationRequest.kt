package net.itanchi.addeep.server.rest.convesation.request

import kotlinx.serialization.Serializable
import net.itanchi.addeep.server.repository.conversation.ConversationType

@Serializable
data class CreateConversationRequest(
    val title: String,
    val type: ConversationType,
    val participantIds: List<Long>
)