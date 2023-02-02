package net.itanchi.addeep.server.rest.point

import kotlinx.coroutines.reactive.awaitFirst
import net.itanchi.addeep.server.rest.point.dto.PointHistoryDTO
import net.itanchi.addeep.server.rest.point.response.PointHistoryResponse
import net.itanchi.addeep.server.service.point.PointService
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class PointHandler(
    private val pointService: PointService,
) {
    companion object {
        const val PAGE_QUERY_KEY = "page"
        const val PAGE_SIZE_QUERY_KEY = "pageSize"
        const val DEFAULT_PAGE_SIZE = 20L
        const val DEFAULT_PAGE = 1L
    }

    suspend fun getPointHistory(request: ServerRequest): ServerResponse {
        val userId = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name.toLong()
        val page = request.queryParamOrNull(PAGE_QUERY_KEY)?.toLongOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull(PAGE_SIZE_QUERY_KEY)?.toLongOrNull() ?: DEFAULT_PAGE_SIZE

        val pointHistory = pointService.getPointHistory(
            userId = userId,
            page = page,
            pageSize = pageSize
        )
        return ServerResponse.ok()
            .json()
            .bodyValueAndAwait(
                PointHistoryResponse(
                    data = pointHistory.map { PointHistoryDTO.fromPointHistory(it) }
                )
            )
    }
}