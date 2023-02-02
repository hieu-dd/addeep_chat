package net.itanchi.addeep.server.rsocket

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.awaitFirst
import net.itanchi.addeep.server.rest.convesation.dto.ConversationDTO
import net.itanchi.addeep.server.rest.event.dto.EventDTO
import net.itanchi.addeep.server.rsocket.request.TriggerMessageEventRequest
import net.itanchi.addeep.server.service.conversation.ConversationService
import net.itanchi.addeep.server.service.conversation.RedisPubSubService
import net.itanchi.addeep.server.service.event.EventService
import net.itanchi.addeep.server.service.notification.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import javax.annotation.PreDestroy


@Controller
class RsocketController(
    private val pubsubService: RedisPubSubService,
    private val eventService: EventService,
) {

    private val logger = LoggerFactory.getLogger(RsocketController::class.java)

    val clients = mutableSetOf<RSocketRequester>()

    private fun getTopic(userId: Long) = "conversations-$userId"


    @PreDestroy
    fun shutdown() {
        clients.stream().forEach { requester -> requester.rsocket()?.dispose() }
    }

    /**
     * Stream conversation message. Message data will be in the following format:
     *
     *   | type      | message                     | attachments                                |
     *   |-----------|-----------------------------|--------------------------------------------|
     *   | PlainText | text content                | no                                         |
     *   | Sticker   | {stickerPack}/{stickerName} | no                                         |
     *   | Gif       | gif url                     | no                                         |
     *   | Document  | no                          | {files: [{rawFileName: uploadedFileName}]} |
     *   | Image     | image caption               | {files: [{rawFileName: uploadedFileName}]} |
     *   | Video     | no                          | {files: [{rawFileName: uploadedFileName}]} |
     */
    @MessageMapping("v1/conversations")
    suspend fun streamConversations(requester: RSocketRequester): Flow<ConversationDTO> {
        val userId = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        requester.rsocket()?.onClose()?.doFirst {
            logger.info("Client: {} CONNECTED.", userId);
            clients.add(requester)
        }?.doOnError { error ->
            logger.warn("Channel to client {} CLOSED with error $error", userId)
        }?.doFinally {
            clients.remove(requester);
            logger.info("Client {} DISCONNECTED", userId)
        }?.subscribe()
        return pubsubService.subscribe(getTopic(userId))
    }

    @MessageMapping("v1/conversations/{conversationId}/messages/{messageId}")
    suspend fun triggerMessageEvent(
        @RequestBody messageEventRequest: TriggerMessageEventRequest,
        @DestinationVariable messageId: Long,
        @DestinationVariable conversationId: Long
    ): List<EventDTO> {
        val userId = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        return eventService.handleMessageEvent(
            userId = userId,
            actionType = messageEventRequest.action,
            conversationId = conversationId,
            messageId = messageId,
        ).map { EventDTO.fromEvent(it) }
    }
}