package net.itanchi.addeep.server.repository.event

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EventActionRepo : CoroutineCrudRepository<EventActionModel, Long>{
    suspend fun findAllByEventIdIn(eventIds: List<Long>): List<EventActionModel>
}