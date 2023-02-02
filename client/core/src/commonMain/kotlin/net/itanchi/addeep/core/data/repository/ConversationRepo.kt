package net.itanchi.addeep.core.data.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.itanchi.addeep.core.data.model.*
import net.itanchi.addeep.core.db.ConversationQueries
import net.itanchi.addeep.core.db.MessageQueries
import net.itanchi.addeep.core.util.transactionWithContext

internal class ConversationRepo(
    private val conversationQueries: ConversationQueries,
    private val messageQueries: MessageQueries,
    private val backgroundDispatcher: CoroutineDispatcher,
    private val json: Json,
) {
    private val conversationMapper = { id: Long,
                                       title: String,
                                       type: String,
                                       creator: Long,
                                       created_at: Long,
                                       updated_at: Long ->
        Conversation(
            id = id,
            title = title,
            type = ConversationType.valueOf(type),
            creator = User(id = creator),
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at),
        )

    }

    private val messageMapper = { id: Long,
                                  localId: String,
                                  conversation: Long,
                                  sender: Long,
                                  message: String,
                                  stickerUrl: String,
                                  attachments: String,
                                  type: String,
                                  status: String,
                                  created_at: Long,
                                  updated_at: Long ->
        Message(
            id = id,
            localId = localId,
            sender = User(id = sender),
            message = message,
            stickerUrl = stickerUrl,
            attachments = Json.decodeFromString(attachments),
            type = MessageType.valueOf(type),
            status = MessageStatus.valueOf(status),
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at),
        )
    }

    // Conversation

    suspend fun findAll(): Flow<List<Conversation>> = conversationQueries.findAll(conversationMapper)
        .asFlow()
        .mapToList()
        .flowOn(backgroundDispatcher)

    suspend fun findById(
        conversationId: Long,
    ): Flow<Conversation> = conversationQueries.findById(conversationId, conversationMapper)
        .asFlow()
        .mapToOne()
        .flowOn(backgroundDispatcher)

    suspend fun findSingleConversationWithUser(
        userId: Long,
    ): Conversation? {
        return conversationQueries.findConversationWithUser(userId, ConversationType.Single.name, conversationMapper)
            .asFlow()
            .mapToOneOrNull(backgroundDispatcher)
            .firstOrNull()
    }

    suspend fun upsert(
        conversation: Conversation,
    ) = conversationQueries.transactionWithContext(backgroundDispatcher) {
        conversationQueries.upsert(
            id = conversation.id,
            title = conversation.title,
            type = conversation.type.name,
            creator = conversation.creator.id,
            created_at = conversation.createdAt.toEpochMilliseconds(),
            updated_at = conversation.updatedAt.toEpochMilliseconds(),
        )
    }

    suspend fun deleteById(
        conversationId: Long,
    ) = conversationQueries.transactionWithContext(backgroundDispatcher) {
        conversationQueries.deleteById(conversationId)
    }

    suspend fun deleteAll() = conversationQueries.transactionWithContext(backgroundDispatcher) {
        conversationQueries.deleteAll()
    }

    // Message

    suspend fun findConversationMessages(
        conversationId: Long,
        limit: Long = 0,
    ): Flow<List<Message>> = if (limit > 0) {
        messageQueries.findByConversation(conversationId, limit, messageMapper)
    } else {
        messageQueries.findAllByConversation(conversationId, messageMapper)
    }
        .asFlow()
        .mapToList()
        .flowOn(backgroundDispatcher)

    suspend fun findConversationMessageById(
        messageId: Long,
    ): Flow<Message> = messageQueries.findById(messageId, messageMapper)
        .asFlow()
        .mapToOne()
        .flowOn(backgroundDispatcher)

    suspend fun upsertConversationMessages(
        conversationId: Long,
        messages: List<Message>,
    ) = messageQueries.transactionWithContext(backgroundDispatcher) {
        messages.forEach { message ->
            val existing = if (message.id != 0L) {
                message.attachments.forEach {
                    it.url = "${message.getMessageUrl(conversationId)}/${it.name}"
                }
                if (message.type == MessageType.Sticker) {
                    val stickerName = message.message.split("/").last()
                    message.stickerUrl = "${message.getMessageUrl(conversationId)}/${stickerName}"
                }
                messageQueries.findById(message.id, messageMapper).executeAsOneOrNull()
            } else {
                null
            }

            messageQueries.upsert(
                id = message.id,
                localId = existing?.localId ?: message.localId,
                conversation = conversationId,
                sender = message.sender.id,
                message = message.message,
                stickerUrl = message.stickerUrl,
                attachments = json.encodeToString(message.attachments),
                type = message.type.name,
                status = message.status.name,
                created_at = message.createdAt.toEpochMilliseconds(),
                updated_at = message.updatedAt.toEpochMilliseconds(),
            )
        }
    }

    suspend fun deleteConversationMessages(
        conversationId: Long,
    ) = messageQueries.transactionWithContext(backgroundDispatcher) {
        messageQueries.deleteAllByConversation(conversationId)
    }

    suspend fun deleteAllMessages() = messageQueries.transactionWithContext(backgroundDispatcher) {
        messageQueries.deleteAll()
    }
}