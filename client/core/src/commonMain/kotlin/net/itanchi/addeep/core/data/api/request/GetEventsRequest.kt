package net.itanchi.addeep.core.data.api.request

import kotlinx.serialization.Serializable

@Serializable
internal data class GetEventsRequest(
    val page: Int = 1,
    val pageSize: Int = 20,
)
