package net.itanchi.addeep.server.rest

import kotlinx.serialization.Serializable

abstract class Response<D> {
    abstract val code: Int
    abstract val message: String
    open val data: D? = null
}

@Serializable
data class ErrorResponse(
    override val code: Int,
    override val message: String,
) : Response<Nothing>()