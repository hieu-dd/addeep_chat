package net.itanchi.addeep.server.repository.contact

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserContactRepo : CoroutineCrudRepository<UserContactModel, Long>{
    suspend fun findAllByUserId(userId: Long): List<UserContactModel>
}