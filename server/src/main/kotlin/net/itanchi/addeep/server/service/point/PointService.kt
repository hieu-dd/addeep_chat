package net.itanchi.addeep.server.service.point

import net.itanchi.addeep.server.repository.event.ActionType
import net.itanchi.addeep.server.repository.point.*
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class PointService(
    private val userPointRepo: UserPointRepo,
    private val pointHistoryRepo: PointHistoryRepo,
) {
    suspend fun getPointInfo(userId: Long): PointInfo {
        return userPointRepo.findByUserId(userId)?.toPointInfo() ?: PointInfo(
            receivedPoint = 0,
            balance = 0,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    suspend fun addPointHistory(
        userId: Long,
        point: Long,
        actionType: ActionType,
        eventId: Long,
    ): PointHistory {
        return pointHistoryRepo.save(
            PointHistoryModel(
                userId = userId,
                point = point,
                actionType = actionType,
                eventId = eventId
            )
        ).toPointHistory()
    }

    suspend fun getPointHistory(
        userId: Long,
        page: Long,
        pageSize: Long
    ): List<PointHistory> {
        return pointHistoryRepo.findAllByUserIdOrOrderByCreatedAtDesc(
            userId = userId,
            offset = (page - 1) * pageSize,
            limit = pageSize
        ).mapIndexed { _, pointHistory -> pointHistory.toPointHistory() }
    }
}