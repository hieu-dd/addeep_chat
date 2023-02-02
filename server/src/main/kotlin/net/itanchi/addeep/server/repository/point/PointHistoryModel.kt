package net.itanchi.addeep.server.repository.point

import net.itanchi.addeep.server.repository.event.ActionType
import net.itanchi.addeep.server.service.event.Action
import net.itanchi.addeep.server.service.point.PointHistory
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("point_history")
data class PointHistoryModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("user_id")
    val userId: Long = 0,

    @Column("point")
    val point: Long = 0,

    @Column("event_id")
    val eventId: Long,

    @Column("action_type")
    val actionType: ActionType,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)

internal fun PointHistoryModel.toPointHistory() = with(this) {
    PointHistory(
        id = id,
        userId = userId,
        point = point,
        actionType = actionType,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}