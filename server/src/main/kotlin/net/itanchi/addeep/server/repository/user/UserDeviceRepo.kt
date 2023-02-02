package net.itanchi.addeep.server.repository.user

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserDeviceRepo : CoroutineCrudRepository<UserDeviceModel, Long> {
    @Query("REPLACE INTO user_devices(user_id, device_token, type, updated_at) VALUES (:userId, :deviceToken, :deviceType, NOW())")
    suspend fun updateDeviceToken(deviceToken: String, deviceType: DeviceType, userId: Long)

    suspend fun findAllByUserId(userId: Long): List<UserDeviceModel>
}