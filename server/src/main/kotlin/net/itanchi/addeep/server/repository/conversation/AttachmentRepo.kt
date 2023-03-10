package net.itanchi.addeep.server.repository.conversation

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AttachmentRepo : CoroutineCrudRepository<AttachmentModel, Long> {
    suspend fun findAllByMessageId(conversationId: Long): List<AttachmentModel>
    suspend fun findAllByMessageIdIn(conversationIds: List<Long>): List<AttachmentModel>
}