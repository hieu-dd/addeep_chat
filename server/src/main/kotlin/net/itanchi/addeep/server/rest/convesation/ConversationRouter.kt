package net.itanchi.addeep.server.rest.convesation

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class ConversationRouter {

    companion object {
        const val API_ROUTE = "/api/v1/conversations"
    }

    @Bean
    fun conversationRoutes(conversationHandler: ConversationHandler) = coRouter {
        (accept(MediaType.APPLICATION_JSON) and API_ROUTE).nest {
            GET("", conversationHandler::getConversations)
            POST("", conversationHandler::createConversation)
            POST("/{conversationId}/messages", conversationHandler::sendMessage)
            GET("/{conversationId}/messages", conversationHandler::getMessageAfter)
            GET("/{conversationId}", conversationHandler::getConversationDetail)
            GET("/{conversationId}/messages/{messageId}/{contentName}", conversationHandler::downloadMessageContent)
        }
    }
}