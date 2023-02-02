package net.itanchi.addeep.core.data.api

import net.itanchi.addeep.core.data.api.request.GetEventsRequest
import net.itanchi.addeep.core.data.api.response.GetEventsResponse

internal interface EventApi {
    suspend fun getEvents(request: GetEventsRequest): GetEventsResponse
}