package net.itanchi.addeep.server.rest.media

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class MediaRouter {
    companion object {
        const val API_ROUTE = "api/v1/media"
    }

    @Bean
    fun mediaRouters(mediaHandler: MediaHandler) = coRouter {
        (accept(MediaType.APPLICATION_JSON) and API_ROUTE).nest {
            GET("gifs", mediaHandler::getGifs)
            GET("gifs/{gifName}", mediaHandler::downloadGif)
        }
    }
}