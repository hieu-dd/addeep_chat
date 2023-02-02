package net.itanchi.addeep.server.repository.contact

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("user_contact")
data class UserContactModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("user_id")
    val userId: Long = 0,

    @Column("contact_id")
    val contactId: Long,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    var updatedAt: Instant = Instant.now(),
)