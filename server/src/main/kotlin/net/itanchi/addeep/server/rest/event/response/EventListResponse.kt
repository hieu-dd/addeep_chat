package net.itanchi.addeep.server.rest.event.response

import kotlinx.serialization.Serializable
import net.itanchi.addeep.server.rest.Response
import net.itanchi.addeep.server.rest.event.dto.EventDTO

@Serializable
data class EventListResponse(
    override val code: Int = 0,
    override val message: String = "success",
    override val data: List<EventDTO> = listOf()
) : Response<List<EventDTO>>()