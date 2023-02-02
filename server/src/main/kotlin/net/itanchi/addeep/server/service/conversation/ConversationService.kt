package net.itanchi.addeep.server.service.conversation

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import net.itanchi.addeep.server.exception.Error
import net.itanchi.addeep.server.repository.conversation.*
import net.itanchi.addeep.server.repository.event.ActionType
import net.itanchi.addeep.server.repository.message.MessageModel
import net.itanchi.addeep.server.repository.message.MessageRepo
import net.itanchi.addeep.server.repository.message.MessageType
import net.itanchi.addeep.server.repository.message.toMessage
import net.itanchi.addeep.server.repository.participant.ParticipantModel
import net.itanchi.addeep.server.repository.participant.ParticipantRepo
import net.itanchi.addeep.server.repository.user.UserModel
import net.itanchi.addeep.server.repository.user.UserRepo
import net.itanchi.addeep.server.rest.convesation.dto.ConversationDTO
import net.itanchi.addeep.server.service.event.EventService
import net.itanchi.addeep.server.service.notification.NotificationService
import net.itanchi.addeep.server.service.storage.GoogleStorageService.Companion.DEFAULT_BUCKET_NAME
import net.itanchi.addeep.server.service.storage.StorageService
import org.apache.tika.Tika
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ConversationService(
    private val participantRepo: ParticipantRepo,
    private val conversationRepo: ConversationRepo,
    private val attachmentRepo: AttachmentRepo,
    private val messageRepo: MessageRepo,
    private val userRepo: UserRepo,
    private val storageService: StorageService,
    private val notificationService: NotificationService,
    private val pubsubService: RedisPubSubService,
    private val eventService: EventService,
) {
    companion object {
        const val CONVERSATION_LIST_MESSAGE_LIMIT = 20
        const val CONVERSATION_LIST_PARTICIPANT_LIMIT = 4
        const val MAX_IMAGE_SIZE = 10L
        const val MAX_FILE_SIZE = 10L
        const val MAX_VIDEO_SIZE = 100L
        const val IMAGE_CONTENT_TYPE_PREFIX = "image/"
        const val VIDEO_CONTENT_TYPE_PREFIX = "video/"
        val NOT_SUPPORT_ATTACHMENTS_MESSAGE_TYPES = listOf(
            MessageType.PlainText, MessageType.Gif, MessageType.Sticker
        )
    }

    suspend fun findUserConversations(userId: Long): List<Conversation> = coroutineScope {
        val conversations = conversationRepo.findAllByUserId(userId)
        val conversationIds = conversations.map { it.id }
        val conversationData = awaitAll(async { participantRepo.findAllByConversationIdIn(conversationIds) }, async {
            if (conversationIds.isNotEmpty()) messageRepo.findLatestMessagesOfConversations(conversationIds)
                .fetchMessageAttachments()
            else listOf()
        })
        val participants = conversationData[0] as List<ParticipantModel>
        val latestMessages = conversationData[1] as List<MessageModel>

        val userIds =
            participants.mapTo(mutableSetOf()) { it.userId } + conversations.mapTo(mutableSetOf()) { it.creatorId } + latestMessages.mapTo(
                mutableSetOf()
            ) { it.senderId }
        val userInfos = userRepo.findAllById(userIds).toList()
        val participantGroups = participants.groupBy { it.conversationId }
        conversations.map { conversation ->
            conversation.toConversation(
                participants = participantGroups[conversation.id]!!.mapNotNull { participant ->
                    userInfos.firstOrNull { userInfo ->
                        userInfo.id == participant.userId
                    }
                }.take(CONVERSATION_LIST_PARTICIPANT_LIMIT),
                messages = latestMessages.filter { it.conversationId == conversation.id },
                creator = userInfos.first { it.id == conversation.creatorId },
                senderInfos = userInfos
            )
        }
    }

    suspend fun findConversationById(
        userId: Long,
        conversationId: Long,
    ): Conversation = coroutineScope {
        val conversation =
            conversationRepo.findById(conversationId) ?: throw  Error.GetConversationDetailError.ConversationNotfound
        val conversationData = awaitAll(async { participantRepo.findAllByConversationId(conversationId) }, async {
            messageRepo.findLatestMessagesOfConversation(
                conversationId = conversationId, limit = CONVERSATION_LIST_MESSAGE_LIMIT
            ).fetchMessageAttachments()
        })
        val participants = conversationData[0] as List<ParticipantModel>
        participants.find { it.userId == userId } ?: throw Error.GetConversationDetailError.ConversationNotfound
        val latestMessages = (conversationData[1] as List<MessageModel>).apply { fetchMessageAttachments() }
        val userIds =
            participants.mapTo(mutableSetOf()) { it.userId } + setOf(conversation.creatorId) + latestMessages.mapTo(
                mutableSetOf()
            ) { it.senderId }

        val userInfos = userRepo.findAllById(userIds).toList()
        conversation.toConversation(
            participants = participants.mapNotNull { participant ->
                userInfos.firstOrNull { userInfo ->
                    userInfo.id == participant.userId
                }
            },
            messages = latestMessages,
            creator = userInfos.first { it.id == conversation.creatorId },
            senderInfos = userInfos
        )
    }

    suspend fun createConversation(
        title: String,
        creatorId: Long,
        conversationType: ConversationType,
        participantIds: List<Long>,
    ): Conversation = coroutineScope {
        val newConversation = ConversationModel(
            title = title,
            type = ConversationType.Single,
            creatorId = creatorId,
        )
        val savedConversation = conversationRepo.save(newConversation)
        val finalParticipants = participantIds.toMutableSet()
        finalParticipants.add(creatorId)
        val availableParticipantIds = userRepo.findAllById(finalParticipants).toList().map { it.id }
        val missingIds =
            finalParticipants.filter { participantId -> availableParticipantIds.all { it != participantId } }
        if (missingIds.isNotEmpty()) throw Error.CreateConversationError.ParticipantsNotfound(missingIds)
        val newParticipants = availableParticipantIds.map {
            ParticipantModel(
                conversationId = savedConversation.id, userId = it
            )
        }
        participantRepo.saveAll(newParticipants).toList()
        findConversationById(
            conversationId = savedConversation.id,
            userId = creatorId,
        )
    }

    suspend fun getMessagesAfter(
        userId: Long, conversationId: Long, messageBefore: Long?, messageAfter: Long?
    ): List<Message> {
        val participants = participantRepo.findAllByConversationId(conversationId)
        participants.find { it.userId == userId } ?: throw Error.GetConversationDetailError.ConversationNotfound
        val messages = when {
            messageAfter != null -> messageRepo.findLatestMessageAfter(
                conversationId = conversationId,
                messageId = messageAfter,
                limit = CONVERSATION_LIST_MESSAGE_LIMIT,
            )
            else -> messageRepo.findLatestMessageBefore(
                conversationId = conversationId,
                messageId = messageBefore ?: Long.MAX_VALUE,
                limit = CONVERSATION_LIST_MESSAGE_LIMIT,
            )
        }.fetchMessageAttachments()
        val senderInfos = messages.map { it.senderId }.takeIf { it.isNotEmpty() }?.let {
            userRepo.findAllById(it).toList()
        }.orEmpty()
        return messages.map { message -> message.toMessage(senderInfos.first { it.id == message.senderId }) }
    }

    suspend fun saveMessage(
        conversationId: Long,
        senderId: Long,
        message: String,
        messageType: MessageType,
        attachments: Attachments,
    ): Conversation = coroutineScope {
        validateAttachments(messageType = messageType, attachments = attachments)
        val participantUserIds = participantRepo.findAllByConversationId(conversationId).map { it.userId }.takeIf {
            it.contains(senderId)
        } ?: throw Error.ChatError.ConversationNotFound(conversationId)
        val saveAttachments =
            attachments.takeIf { !NOT_SUPPORT_ATTACHMENTS_MESSAGE_TYPES.contains(messageType) }?.files?.map {
                async {
                    val fileName = "${Instant.now().epochSecond}_${senderId}_${it.filename}"
                    val contentType = Tika().detect(it.data.first().asInputStream())
                    val uploadInfo = storageService.uploadFile(
                        bucketName = DEFAULT_BUCKET_NAME,
                        path = "conversations/$conversationId/attachments/$fileName",
                        contentType = contentType,
                        data = it.data.map { it.asByteBuffer() },
                        limitSize = getLimitSize(
                            messageType = messageType,
                            contentType = contentType
                        ) * 1024 * 1024
                    )
                    AttachmentModel(
                        messageId = conversationId,
                        type = contentType,
                        originalName = it.filename,
                        name = fileName,
                        size = uploadInfo.size,
                        md5 = uploadInfo.md5
                    )
                }
            }?.awaitAll()
        val savedMessage = messageRepo.save(
            MessageModel(
                conversationId = conversationId,
                senderId = senderId,
                message = message,
                messageType = messageType,
            )
        )
        saveAttachments?.map { it.apply { messageId = savedMessage.id } }?.takeIf { it.isNotEmpty() }?.let {
            attachmentRepo.saveAll(it).toList()
        }
        conversationRepo.findById(conversationId)?.let { conversationModel ->
            val userInfos: List<UserModel>
            val participants: List<UserModel>
            when (conversationModel.type) {
                ConversationType.Single -> {
                    userInfos =
                        userRepo.findAllById(setOf(conversationModel.creatorId, senderId) + participantUserIds).toList()
                    participants = userInfos
                }
                else -> {
                    userInfos = userRepo.findAllById(setOf(conversationModel.creatorId, senderId)).toList()
                    participants = participantUserIds.map {
                        UserModel(
                            id = it, countryCode = "", email = "", name = "", phoneNumber = ""
                        )
                    }
                }
            }

            conversationModel.toConversation(participants = participants,
                messages = listOf(savedMessage.apply { this@apply.attachments = saveAttachments.orEmpty() }),
                creator = userInfos.first { it.id == conversationModel.creatorId },
                senderInfos = userInfos.filter { it.id == senderId }).also {
                launch {
                    conversationRepo.save(conversationModel.apply { updatedAt = Instant.now() })
                }
                launch {
                    notifyParticipants(
                        conversation = it
                    )
                }
                launch {
                    pushNotifications(
                        conversation = it
                    )
                }
                launch {
                    eventService.handleMessageEvent(
                        userId = senderId,
                        actionType = ActionType.Send,
                        messageId = it.messages.first().id,
                        conversationId = conversationId
                    )
                }
            }
        } ?: throw Error.ChatError.ConversationNotFound(conversationId)
    }

    suspend fun getMessageContentPath(
        userId: Long, conversationId: Long, messageId: Long, contentName: String
    ): String {
        val participants = participantRepo.findAllByConversationId(conversationId)
        participants.find { it.userId == userId } ?: throw Error.DownloadMessageContentError.InvalidContent
        val message = messageRepo.findById(messageId) ?: throw Error.DownloadMessageContentError.InvalidContent
        return when (message.messageType) {
            MessageType.Sticker -> "stickers/${message.message}"
            MessageType.PlainText -> throw Error.DownloadMessageContentError.InvalidContent
            else -> "conversations/${conversationId}/attachments/$contentName"
        }
    }

    private fun validateAttachments(
        messageType: MessageType,
        attachments: Attachments,
    ) {
        when {
            !isValidContentType(
                messageType = messageType,
                attachments = attachments,
            ) -> throw Error.UploadFileError.InvalidFile
            attachments.files.any {
                it.contentLength > getLimitSize(
                    messageType = messageType,
                    contentType = it.contentType
                ) * 1024 * 1024
            }
            -> Error.UploadFileError.ExceedMaxSize
        }
    }

    private fun isValidContentType(
        messageType: MessageType,
        attachments: Attachments,
    ): Boolean {
        return when (messageType) {
            MessageType.Photo -> attachments.files.all {
                (it.contentType?.startsWith(IMAGE_CONTENT_TYPE_PREFIX) ?: false)
                        || (it.contentType?.startsWith(VIDEO_CONTENT_TYPE_PREFIX) ?: false)
            }
            else -> true
        }
    }

    private fun notifyParticipants(
        conversation: Conversation
    ) {
        val participantIds = conversation.participants.map { it.id }
        participantIds.forEach {
            pubsubService.publish(ConversationDTO.fromConversation(conversation), getUserTopic(it))
        }
    }

    private suspend fun pushNotifications(
        conversation: Conversation,
    ) = coroutineScope {
        val senderId = conversation.messages.first().sender.id
        val participantIds = conversation.participants.mapNotNull { user ->
            user.id.takeIf { it != senderId }
        }
        participantIds.map {
            async {
                notificationService.pushNotification(
                    userId = it, data = conversation
                )
            }
        }.awaitAll()
    }

    fun getLimitSize(
        messageType: MessageType,
        contentType: String?
    ): Long {
        return when {
            messageType == MessageType.Photo && contentType?.startsWith(IMAGE_CONTENT_TYPE_PREFIX) ?: false
            -> MAX_IMAGE_SIZE
            messageType == MessageType.Photo && contentType?.startsWith(VIDEO_CONTENT_TYPE_PREFIX) ?: false
            -> MAX_VIDEO_SIZE
            messageType == MessageType.Document -> MAX_FILE_SIZE
            else -> 0
        }
    }

    private fun getUserTopic(userId: Long) = "conversations-$userId"

    private suspend fun List<MessageModel>.fetchMessageAttachments(): List<MessageModel> {
        val attachmentGroups = takeIf { isNotEmpty() }?.map { it.id }
            ?.let { attachmentRepo.findAllByMessageIdIn(it).groupBy { it.messageId } }.orEmpty()
        return map {
            it.apply {
                attachments = attachmentGroups[it.id].orEmpty()
            }
        }
    }
}