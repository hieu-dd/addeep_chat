package net.itanchi.addeep.server.rest.point

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class PointRouter {

    companion object {
        const val API_ROUTE = "/api/v1/points"
    }

    @Bean
    fun pointRoutes(pointHandler: PointHandler) = coRouter {
        (accept(MediaType.APPLICATION_JSON) and API_ROUTE).nest {
            GET("history", pointHandler::getPointHistory)
        }
    }
}