package net.itanchi.addeep.server.service.point

import net.itanchi.addeep.server.repository.event.ActionType
import java.time.Instant

data class PointHistory(
    val id: Long = 0,
    val userId: Long = 0,
    val point: Long = 0,
    val actionType: ActionType = ActionType.Send,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
)