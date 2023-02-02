package net.itanchi.addeep.core.data.api.request

import kotlinx.serialization.Serializable
import net.itanchi.addeep.core.data.model.ActionType

@Serializable
internal data class TriggerConversationMessageRequest(
    val conversationId: Long,
    val messageId: Long,
    val action: ActionType = ActionType.View,
)
