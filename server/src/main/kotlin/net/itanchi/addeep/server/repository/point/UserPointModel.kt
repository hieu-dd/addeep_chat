package net.itanchi.addeep.server.repository.point

import net.itanchi.addeep.server.service.point.PointInfo
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("user_point")
data class UserPointModel(
    @Id
    @Column("user_id")
    val userId: Long = 0,

    @Column("received_point")
    val receivedPoint: Long = 0,

    @Column("balance")
    val balance: Long = 0,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)

fun UserPointModel.toPointInfo() = PointInfo(
    receivedPoint = receivedPoint,
    balance = balance,
    createdAt = createdAt,
    updatedAt = updatedAt
)