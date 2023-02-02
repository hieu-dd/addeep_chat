package net.itanchi.addeep.server.rest.user.dto

import kotlinx.serialization.Serializable
import net.itanchi.addeep.server.service.point.PointInfo

@Serializable
data class PointInfoDTO(
    val receivedPoint: Long,
    val balance: Long,
) {
    companion object {
        fun fromPointInfo(pointInfo: PointInfo) = with(pointInfo) {
            PointInfoDTO(
                receivedPoint = receivedPoint,
                balance = balance,
            )
        }
    }
}