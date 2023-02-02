package net.itanchi.addeep.server.repository.event

import net.itanchi.addeep.server.service.event.Action
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("event_actions")
data class EventActionModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("event_id")
    val eventId: Long,

    @Column("type")
    val type: ActionType,

    @Column("points")
    val points: Long,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)

internal fun EventActionModel.toAction(): Action = Action(
    type = type,
    points = points,
)

enum class ActionType {
    Send,
    View
}