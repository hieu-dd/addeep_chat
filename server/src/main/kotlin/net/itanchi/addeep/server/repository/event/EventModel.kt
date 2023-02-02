package net.itanchi.addeep.server.repository.event

import net.itanchi.addeep.server.repository.message.MessageType
import net.itanchi.addeep.server.service.event.Event
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("events")
data class EventModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("name")
    val name: String,

    @Column("description")
    val description: String,

    @Column("image_url")
    val imageUrl: String,

    @Column("url")
    val url: String,

    @Column("started_at")
    val startedAt: Instant = Instant.now(),

    @Column("ended_at")
    val endedAt: Instant? = null,

    @Column("apply_on")
    val applyOn: MessageType,

    @Column("status")
    val status: Status,

    @Column("created_at")
    @LastModifiedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)

fun EventModel.toEvent(): Event = Event(
    id = id,
    name = name,
    description = description,
    imageUrl = imageUrl,
    url = url,
    startedAt = startedAt,
    endedAt = endedAt,
    applyOn = applyOn,
    status = status,
    createdAt = createdAt,
    updatedAt = createdAt,
)

enum class Status {
    Active,
    InActive
}