package net.itanchi.addeep.server.repository.point

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserPointRepo : CoroutineCrudRepository<UserPointModel, Long> {
    suspend fun findByUserId(userId: Long): UserPointModel?
}