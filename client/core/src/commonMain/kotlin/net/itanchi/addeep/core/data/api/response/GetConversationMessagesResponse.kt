package net.itanchi.addeep.core.data.api.response

import kotlinx.serialization.Serializable
import net.itanchi.addeep.core.data.model.Message

@Serializable
internal data class GetConversationMessagesResponse(
    override val code: Int,
    override val message: String,
    override val data: List<Message>? = null,
) : Response<List<Message>>()