package net.itanchi.addeep.core.data.api.request

import kotlinx.serialization.Serializable
import net.itanchi.addeep.core.data.model.Message

@Serializable
internal data class SendConversationMessageRequest(
    val conversationId: Long,
    val message: Message,
)