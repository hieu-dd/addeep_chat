package net.itanchi.addeep.core.data.api

import co.touchlab.stately.ensureNeverFrozen
import io.ktor.client.*
import io.ktor.client.request.*
import net.itanchi.addeep.core.data.api.request.GetPointsHistoryRequest
import net.itanchi.addeep.core.data.api.response.GetPointsHistoryResponse

internal class PointApiImpl(
    private val client: HttpClient,
) : PointApi {
    companion object {
        private const val API_PATH_POINTS = "/api/v1/points"
    }

    init {
        ensureNeverFrozen()
    }

    override suspend fun getPointsHistory(request: GetPointsHistoryRequest): GetPointsHistoryResponse {
        return client.get("${API_PATH_POINTS}/history") {
            parameter("page", request.page)
            parameter("pageSize", request.pageSize)
        }
    }

}