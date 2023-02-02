package net.itanchi.addeep.server.repository.conversation

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ConversationRepo : CoroutineCrudRepository<ConversationModel, Long> {
    @Query("SELECT * FROM conversations WHERE id IN (SELECT conversation_id FROM participants WHERE user_id = :userId) ORDER BY updated_at DESC")
    suspend fun findAllByUserId(userId: Long): List<ConversationModel>
}