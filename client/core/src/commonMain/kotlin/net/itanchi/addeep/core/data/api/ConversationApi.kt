package net.itanchi.addeep.core.data.api

import kotlinx.coroutines.flow.Flow
import net.itanchi.addeep.core.data.api.request.*
import net.itanchi.addeep.core.data.api.response.*
import net.itanchi.addeep.core.data.model.Conversation
import net.itanchi.addeep.core.data.model.Event

internal interface ConversationApi {
    // Rest

    suspend fun getConversations(): GetConversationsResponse

    suspend fun getConversation(
        request: GetConversationRequest,
    ): GetConversationResponse

    suspend fun createConversation(
        request: CreateConversationRequest,
    ): CreateConversationResponse

    suspend fun getConversationMessages(
        request: GetConversationMessagesRequest,
    ): GetConversationMessagesResponse

    suspend fun sendConversationMessage(
        request: SendConversationMessageRequest,
    ): SendConversationMessageResponse

    // Socket

    suspend fun triggerConversationMessage(
        token: String,
        request: TriggerConversationMessageRequest,
    ): List<Event>

    suspend fun streamConversations(
        token: String,
    ): Flow<Conversation>

    suspend fun stopStreamingConversation()

}