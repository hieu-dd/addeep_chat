package net.itanchi.addeep.core.data.api.response

import kotlinx.serialization.Serializable
import net.itanchi.addeep.core.data.model.Event

@Serializable
internal data class GetEventsResponse(
    override val code: Int,
    override val message: String,
    override val data: List<Event>? = null,
) : Response<List<Event>>()