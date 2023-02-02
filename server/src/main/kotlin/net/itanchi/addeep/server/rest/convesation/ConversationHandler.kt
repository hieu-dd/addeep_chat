package net.itanchi.addeep.server.rest.convesation

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import net.itanchi.addeep.server.exception.Error
import net.itanchi.addeep.server.rest.CustomHeaders.DOWNLOAD_FILE_HEADER
import net.itanchi.addeep.server.rest.convesation.dto.ConversationDTO
import net.itanchi.addeep.server.rest.convesation.dto.MessageDTO
import net.itanchi.addeep.server.rest.convesation.request.CreateConversationRequest
import net.itanchi.addeep.server.rest.convesation.response.*
import net.itanchi.addeep.server.service.conversation.Attachments
import net.itanchi.addeep.server.service.conversation.ConversationService
import net.itanchi.addeep.server.service.conversation.FileAttachment
import net.itanchi.addeep.server.service.event.EventService
import net.itanchi.addeep.server.service.storage.StorageService
import net.itanchi.addeep.server.utils.converters.toMessageType
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class ConversationHandler(
    private val conversationService: ConversationService,
    private val storageService: StorageService,
    private val eventService: EventService,
) {
    companion object {
        const val MESSAGE_TYPE_PART_NAME = "type"
        const val MESSAGE_PART_NAME = "message"
        const val ATTACHMENTS_PART_NAME = "attachments"
        const val MAX_ATTACHMENT = 10
    }

    suspend fun getConversations(request: ServerRequest): ServerResponse {
        val userId = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        return ServerResponse.ok()
            .json()
            .bodyValueAndAwait(
                ConversationListResponse(
                    data = conversationService.findUserConversations(userId)
                        .map { ConversationDTO.fromConversation(it) },
                )
            )
    }

    suspend fun getConversationDetail(request: ServerRequest): ServerResponse {
        val userId = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        val conversationId = request.pathVariable("conversationId").toLongOrNull()
            ?: throw Error.GetConversationDetailError.InvalidConversationId
        return ServerResponse.ok()
            .json()
            .bodyValueAndAwait(
                ConversationDetailResponse(
                    data = ConversationDTO.fromConversation(
                        conversation = conversationService.findConversationById(
                            userId = userId,
                            conversationId = conversationId
                        )
                    )
                )
            )
    }

    suspend fun createConversation(request: ServerRequest): ServerResponse {
        val userId = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        val createConversationRequest = request.awaitBody<CreateConversationRequest>()
        return ServerResponse.ok()
            .json()
            .bodyValueAndAwait(
                CreateConversationResponse(
                    data = ConversationDTO.fromConversation(
                        conversation = conversationService.createConversation(
                            title = createConversationRequest.title,
                            creatorId = userId,
                            conversationType = createConversationRequest.type,
                            participantIds = createConversationRequest.participantIds
                        )
                    )
                )
            )
    }

    suspend fun getMessageAfter(request: ServerRequest): ServerResponse {
        val userId = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        val conversationId = request.pathVariable("conversationId").toLongOrNull()
            ?: throw Error.GetConversationDetailError.InvalidConversationId
        val messageBefore = request.queryParamOrNull("messageBefore")?.toLongOrNull()
        val messageAfter = request.queryParamOrNull("messageAfter")?.toLongOrNull()

        return ServerResponse.ok()
            .json()
            .bodyValueAndAwait(
                MessagesResponse(
                    data = conversationService.getMessagesAfter(
                        userId = userId,
                        conversationId = conversationId,
                        messageBefore = messageBefore,
                        messageAfter = messageAfter,
                    ).map {
                        MessageDTO.fromMessage(it)
                    }

                )
            )
    }

    suspend fun downloadMessageContent(request: ServerRequest): ServerResponse {
        val userId = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        val conversationId = request.pathVariable("conversationId").toLongOrNull()
            ?: throw Error.DownloadMessageContentError.InvalidConversation
        val messageId = request.pathVariable("messageId").toLongOrNull()
            ?: throw Error.DownloadMessageContentError.InvalidMessage
        val contentName = request.pathVariable("contentName")
        val contentPath = conversationService.getMessageContentPath(
            userId = userId,
            conversationId = conversationId,
            messageId = messageId,
            contentName = contentName
        )
        val resourceInfo = storageService.getResource(contentPath)
        return ServerResponse.ok()
            .header(HttpHeaders.CONTENT_TYPE, resourceInfo.contentType)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$contentName")
            .header(DOWNLOAD_FILE_HEADER, contentName)
            .bodyValueAndAwait(
                InputStreamResource(resourceInfo.data)
            )
    }

    /**
     * Send message and return messageId. Request must follow the format bellow:
     *   | type      | message                     | attachments       |
     *   |-----------|-----------------------------|-------------------|
     *   | PlainText | text content                | no                |
     *   | Sticker   | {stickerPack}/{stickerName} | no                |
     *   | Gif       | gif url                     | no                |
     *   | Document  | no                          | list of documents |
     *   | Image     | image caption               | list of images    |
     *   | Video     | no                          | list of videos    |
     * Each attachment must contain Content-Disposition and Content-Type header
     */
    suspend fun sendMessage(request: ServerRequest): ServerResponse = coroutineScope {
        val userId = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        val conversationId =
            request.pathVariable("conversationId").let {
                it.toLongOrNull() ?: throw Error.ChatError.InvalidChatData
            }
        val multipartData = request.multipartData().awaitFirstOrNull() ?: throw Error.ChatError.InvalidChatData
        val message = (multipartData[MESSAGE_PART_NAME]?.firstOrNull() as? FormFieldPart)?.value().orEmpty()
        val messageType =
            (multipartData[MESSAGE_TYPE_PART_NAME]?.firstOrNull() as? FormFieldPart)?.value()?.toMessageType()
                ?: throw Error.ChatError.InvalidChatData
        val attachments =
            (multipartData[ATTACHMENTS_PART_NAME] as? List<FilePart>).orEmpty().takeIf { it.size <= MAX_ATTACHMENT }
                ?: throw Error.ChatError.ExceedMaxAttachment
        val notifyConversation = conversationService.saveMessage(
            conversationId = conversationId,
            senderId = userId,
            message = message,
            messageType = messageType,
            attachments = Attachments(
                files = attachments.map {
                    FileAttachment(
                        filename = it.filename(),
                        contentType = it.headers().contentType?.toString(),
                        data = it.content().asFlow(),
                        contentLength = it.headers().contentLength
                    )
                }
            )
        )
        notifyConversation.messages.first().let {
            ServerResponse.ok()
                .bodyValueAndAwait(
                    SendMessageResponse(
                        data = MessageDTO.fromMessage(it)
                    )
                )
        }
    }
}