package net.itanchi.addeep.server.service.point

import java.time.Instant

data class PointInfo(
    val receivedPoint: Long = 0,
    val balance: Long = 0,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)