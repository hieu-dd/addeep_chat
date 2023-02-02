package net.itanchi.addeep.core.data.api.request

import kotlinx.serialization.Serializable
import net.itanchi.addeep.core.data.model.ConversationType

@Serializable
internal data class CreateConversationRequest(
    val title: String,
    val type: ConversationType,
    val participantIds: List<Long> = listOf(),
)
