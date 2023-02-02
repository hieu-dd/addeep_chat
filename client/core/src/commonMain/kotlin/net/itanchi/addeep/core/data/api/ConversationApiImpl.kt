package net.itanchi.addeep.core.data.api

import co.touchlab.stately.ensureNeverFrozen
import decodeFromPayload
import encodeToPayload
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.transport.ktor.client.rSocket
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.job
import kotlinx.serialization.json.Json
import net.itanchi.addeep.core.data.api.request.*
import net.itanchi.addeep.core.data.api.response.*
import net.itanchi.addeep.core.data.model.Conversation
import net.itanchi.addeep.core.data.model.Event
import net.itanchi.addeep.core.data.model.MessageType

internal class ConversationApiImpl(
    private val client: HttpClient,
    private val mediaClient: HttpClient,
    private val rSocketClient: HttpClient,
    private val json: Json,
    private val apiConfig: ApiConfig,
) : ConversationApi {
    companion object {
        private const val API_PATH_CONVERSATIONS = "/api/v1/conversations"
        private const val SOCKET_PATH_CONVERSATIONS = "v1/conversations"
    }

    private var rSocket: RSocket? = null

    init {
        ensureNeverFrozen()
    }

    // Rest

    override suspend fun getConversations(): GetConversationsResponse {
        return client.get(API_PATH_CONVERSATIONS)
    }

    override suspend fun getConversation(
        request: GetConversationRequest,
    ): GetConversationResponse {
        return client.get("$API_PATH_CONVERSATIONS/${request.conversationId}")
    }

    override suspend fun createConversation(
        request: CreateConversationRequest,
    ): CreateConversationResponse {
        return client.post(API_PATH_CONVERSATIONS) {
            body = request
        }
    }

    override suspend fun getConversationMessages(
        request: GetConversationMessagesRequest,
    ): GetConversationMessagesResponse {
        return client.get("$API_PATH_CONVERSATIONS/${request.conversationId}/messages") {
            parameter("messageBefore", request.messageBefore)
            parameter("messageAfter", request.messageAfter)
        }
    }

    override suspend fun sendConversationMessage(
        request: SendConversationMessageRequest,
    ): SendConversationMessageResponse {
        return mediaClient.submitFormWithBinaryData(
            url = "$API_PATH_CONVERSATIONS/${request.conversationId}/messages",
            formData = formData {
                append("message", request.message.message)
                append("type", request.message.type.name)
                when (request.message.type) {
                    MessageType.Photo,
                    MessageType.Document -> {
                        request.message.attachments.forEach { attachment ->
                            append("attachments", attachment.contents, Headers.build {
                                append(HttpHeaders.ContentType, attachment.type)
                                append(HttpHeaders.ContentLength, attachment.size)
                                append(HttpHeaders.ContentDisposition, "filename=${attachment.originalName}")
                            })
                        }
                    }
                    else -> {}
                }
            }
        )
    }

    // Socket

    override suspend fun triggerConversationMessage(
        token: String,
        request: TriggerConversationMessageRequest,
    ): List<Event> {
        return currentRSocket().requestResponse(
            json.encodeToPayload(
                route = "$SOCKET_PATH_CONVERSATIONS/${request.conversationId}/messages/${request.messageId}",
                token = token,
                value = request,
            )
        ).let { payload -> json.decodeFromPayload(payload) }
    }

    override suspend fun streamConversations(
        token: String,
    ): Flow<Conversation> {
        return currentRSocket().requestStream(
            json.encodeToPayload<StreamConversationRequest>(
                route = SOCKET_PATH_CONVERSATIONS,
                token = token,
                value = null
            )
        ).map { payload -> json.decodeFromPayload(payload) }
    }

    override suspend fun stopStreamingConversation() {
        rSocket?.coroutineContext?.job?.cancelAndJoin()
        rSocket = null
    }

    private suspend fun currentRSocket(): RSocket {
        return rSocket ?: rSocketClient.rSocket(
            host = apiConfig.host,
            port = apiConfig.port,
            secure = apiConfig.secure,
            path = "/rsocket"
        ).also { rSocket = it }
    }
}