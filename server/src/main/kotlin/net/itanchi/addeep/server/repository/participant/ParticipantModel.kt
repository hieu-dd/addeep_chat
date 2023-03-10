package net.itanchi.addeep.server.repository.participant

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("participants")
data class ParticipantModel(
    @Id
    @Column("id")
    var id: Long = 0,

    @Column("conversation_id")
    var conversationId: Long,

    @Column("user_id")
    var userId: Long,

    @Column("created_at")
    @CreatedDate
    var createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    var updatedAt: Instant = Instant.now(),
)