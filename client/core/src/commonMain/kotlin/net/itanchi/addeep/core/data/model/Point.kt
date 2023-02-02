package net.itanchi.addeep.core.data.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Point(
    val id: Long = 0,
    val userId: Long = 0,
    val point: Long = 0,
    val actionType: ActionType = ActionType.View,
    val createdAt: Instant,
    val updatedAt: Instant,
)

enum class ActionType {
    Send,
    View
}