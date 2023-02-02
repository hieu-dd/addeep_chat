package net.itanchi.addeep.server.rest.convesation.response

import kotlinx.serialization.Serializable
import net.itanchi.addeep.server.rest.Response
import net.itanchi.addeep.server.rest.convesation.dto.ConversationDTO

@Serializable
data class CreateConversationResponse(
    override val code: Int = 0,
    override val message: String = "success",
    override val data: ConversationDTO
) : Response<ConversationDTO>()