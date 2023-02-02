package net.itanchi.addeep.server.rest.user

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class UserRouter {

    companion object {
        const val API_ROUTE = "/api/v1/users"
    }

    @Bean
    fun userRoutes(userHandler: UserHandler) = coRouter {
        (accept(MediaType.APPLICATION_JSON) and API_ROUTE).nest {
            GET("", userHandler::userInfo)
            POST("contacts", userHandler::syncContacts)
            PUT("/push-token", userHandler::pushToken)
            PUT("", userHandler::updateUser)
            POST("avatar", userHandler::uploadAvatar)
            GET("avatar", userHandler::downloadAvatar)
            POST("add-contact", userHandler::addContact)
        }
    }
}