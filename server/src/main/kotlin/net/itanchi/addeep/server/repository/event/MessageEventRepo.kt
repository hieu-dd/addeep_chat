package net.itanchi.addeep.server.repository.event

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageEventRepo : CoroutineCrudRepository<MessageEventModel, Long> {
    suspend fun findAllByEventIdIn(eventIds: List<Long>): List<MessageEventModel>
}