package net.itanchi.addeep.core.data.api.response

import kotlinx.serialization.Serializable
import net.itanchi.addeep.core.data.model.Conversation

@Serializable
internal data class GetConversationResponse(
    override val code: Int,
    override val message: String,
    override val data: Conversation? = null,
) : Response<Conversation>()