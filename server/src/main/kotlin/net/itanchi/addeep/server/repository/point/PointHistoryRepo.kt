package net.itanchi.addeep.server.repository.point

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PointHistoryRepo : CoroutineCrudRepository<PointHistoryModel, Long> {
    @Query("SELECT * FROM point_history WHERE user_id = :userId ORDER BY id DESC LIMIT :offset,:limit")
    suspend fun findAllByUserIdOrOrderByCreatedAtDesc(userId: Long, limit: Long, offset: Long): List<PointHistoryModel>
}