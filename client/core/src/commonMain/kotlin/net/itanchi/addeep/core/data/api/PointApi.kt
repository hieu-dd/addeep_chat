package net.itanchi.addeep.core.data.api

import net.itanchi.addeep.core.data.api.request.GetPointsHistoryRequest
import net.itanchi.addeep.core.data.api.response.GetPointsHistoryResponse

internal interface PointApi {
    suspend fun getPointsHistory(request: GetPointsHistoryRequest): GetPointsHistoryResponse
}