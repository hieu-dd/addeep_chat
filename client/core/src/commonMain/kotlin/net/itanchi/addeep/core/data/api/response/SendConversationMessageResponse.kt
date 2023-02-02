package net.itanchi.addeep.core.data.api.response

import kotlinx.serialization.Serializable
import net.itanchi.addeep.core.data.model.Message

@Serializable
internal data class SendConversationMessageResponse(
    override val code: Int,
    override val message: String,
    override val data: Message? = null,
) : Response<Message>()