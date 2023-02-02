package net.itanchi.addeep.server.repository.event

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface EventRepo : CoroutineCrudRepository<EventModel, Long>{
    suspend fun findAllByStatusOrderByCreatedAtDesc(status: Status): List<EventModel>

    @Query(
        """
            SELECT * FROM events 
            WHERE (:startedFrom is NULL  OR started_at > :startedFrom) 
            AND (:startedTo is NULL  OR started_at < :startedTo) 
            AND (:status is NULL  or status = :status) 
            LIMIT :offset,:limit
        """
    )
    suspend fun findAllByStartedAtGreaterThanEqualAndAndCreatedAtLessThanEqualAndStatus(
        startedFrom: Instant,
        startedTo: Instant,
        status: Status?,
        offset: Long,
        limit: Long,
    ): List<EventModel>
}