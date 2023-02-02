package net.itanchi.addeep.server.rest.point.response

import kotlinx.serialization.Serializable
import net.itanchi.addeep.server.rest.Response
import net.itanchi.addeep.server.rest.point.dto.PointHistoryDTO

@Serializable
data class PointHistoryResponse(
    override val code: Int = 0,
    override val message: String = "success",
    override val data: List<PointHistoryDTO> = listOf()
) : Response<List<PointHistoryDTO>>()