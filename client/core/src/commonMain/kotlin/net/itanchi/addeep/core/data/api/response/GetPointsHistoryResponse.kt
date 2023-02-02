package net.itanchi.addeep.core.data.api.response

import kotlinx.serialization.Serializable
import net.itanchi.addeep.core.data.model.Point

@Serializable
internal data class GetPointsHistoryResponse(
    override val code: Int,
    override val message: String,
    override val data: List<Point>? = null,
) : Response<List<Point>>()