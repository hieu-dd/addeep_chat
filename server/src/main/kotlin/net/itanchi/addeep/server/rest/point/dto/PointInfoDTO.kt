package net.itanchi.addeep.server.rest.point.dto

import kotlinx.serialization.Serializable
import net.itanchi.addeep.server.repository.event.ActionType
import net.itanchi.addeep.server.service.point.PointHistory
import net.itanchi.addeep.server.utils.serializers.InstantSerializer
import java.time.Instant

@Serializable
data class PointHistoryDTO(
    val id: Long = 0,
    val userId: Long = 0,
    val point: Long = 0,
    val actionType: ActionType = ActionType.View,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant,
    @Serializable(with = InstantSerializer::class)
    val updatedAt: Instant,
) {
    companion object {
        fun fromPointHistory(pointHistory: PointHistory) = with(pointHistory) {
            PointHistoryDTO(
                id = id,
                userId = userId,
                point = point,
                actionType = actionType,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}