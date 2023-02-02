package net.itanchi.addeep.core.data.api.request

import kotlinx.serialization.Serializable

@Serializable
internal data class GetConversationMessagesRequest(
    val conversationId: Long,
    val messageBefore: Long?,
    val messageAfter: Long?,
)
