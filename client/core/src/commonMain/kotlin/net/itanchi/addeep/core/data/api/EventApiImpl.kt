package net.itanchi.addeep.core.data.api

import co.touchlab.stately.ensureNeverFrozen
import io.ktor.client.*
import io.ktor.client.request.*
import net.itanchi.addeep.core.data.api.request.GetEventsRequest
import net.itanchi.addeep.core.data.api.response.GetEventsResponse

internal class EventApiImpl(
    private val client: HttpClient,
) : EventApi {
    companion object {
        private const val API_PATH_POINTS = "/api/v1/events"
    }

    init {
        ensureNeverFrozen()
    }

    override suspend fun getEvents(request: GetEventsRequest): GetEventsResponse {
        return client.get(API_PATH_POINTS) {
            parameter("page", request.page)
            parameter("pageSize", request.pageSize)
        }
    }

}