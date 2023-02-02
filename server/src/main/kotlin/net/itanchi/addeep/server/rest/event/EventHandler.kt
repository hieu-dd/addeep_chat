package net.itanchi.addeep.server.rest.event

import net.itanchi.addeep.server.rest.event.dto.EventDTO
import net.itanchi.addeep.server.rest.event.response.EventListResponse
import net.itanchi.addeep.server.service.event.EventService
import net.itanchi.addeep.server.utils.converters.toInstant
import net.itanchi.addeep.server.utils.converters.toStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import java.time.Duration
import java.time.Instant

@Component
class EventHandler(
    private val eventService: EventService,
) {
    companion object {
        const val MAX_QUERY_DURATION_DAYS = 14L
        const val DEFAULT_PAGE = 1L
        const val DEFAULT_PAGE_SIZE = 20L
    }

    suspend fun getEvents(request: ServerRequest): ServerResponse {
        val minStartedFrom = (Instant.now() - Duration.ofDays(MAX_QUERY_DURATION_DAYS))
        val maxStartedTo = (Instant.now() + Duration.ofDays(MAX_QUERY_DURATION_DAYS))
        val startedFrom =
            request.queryParamOrNull("startedFrom")?.toInstant()?.takeIf { it > minStartedFrom } ?: minStartedFrom
        val startedTo =
            request.queryParamOrNull("startedTo")?.toInstant()?.takeIf { it < maxStartedTo } ?: maxStartedTo
        val status = request.queryParamOrNull("status")?.toStatus()
        val page = request.queryParamOrNull("page")?.toLongOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toLongOrNull() ?: DEFAULT_PAGE_SIZE
        val events = eventService.getEvents(
            startedFrom = startedFrom,
            startedTo = startedTo,
            status = status,
            page = page,
            pageSize = pageSize,
        )
        return ServerResponse.ok()
            .json()
            .bodyValueAndAwait(
                EventListResponse(
                    data = events.map { EventDTO.fromEvent(it) }
                )
            )
    }
}