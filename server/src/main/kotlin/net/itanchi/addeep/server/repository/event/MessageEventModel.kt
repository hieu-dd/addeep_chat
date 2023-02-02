package net.itanchi.addeep.server.repository.event

import net.itanchi.addeep.server.service.event.MessageCondition
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("message_event")
data class MessageEventModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("event_id")
    val eventId: Long,

    @Column("sticker_id")
    val stickerId: Long?,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)

internal fun MessageEventModel.toMessageCondition(): MessageCondition = MessageCondition(
    stickerId = stickerId
)