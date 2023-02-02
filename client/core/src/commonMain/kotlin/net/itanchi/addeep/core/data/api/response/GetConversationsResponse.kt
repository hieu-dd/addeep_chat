package net.itanchi.addeep.core.data.api.response

import kotlinx.serialization.Serializable
import net.itanchi.addeep.core.data.model.Conversation

@Serializable
internal data class GetConversationsResponse(
    override val code: Int,
    override val message: String,
    override val data: List<Conversation>? = null,
) : Response<List<Conversation>>()