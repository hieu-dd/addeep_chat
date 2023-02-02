package net.itanchi.addeep.server.rest.event

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class EventRouter {

    companion object {
        const val API_ROUTE = "/api/v1/events"
    }

    @Bean
    fun eventRoutes(eventHandler: EventHandler) = coRouter {
        (accept(MediaType.APPLICATION_JSON) and API_ROUTE).nest {
            GET("", eventHandler::getEvents)
        }
    }
}