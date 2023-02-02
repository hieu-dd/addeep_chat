package net.itanchi.addeep.core.data.api.response

internal abstract class Response<D> {
    abstract val code: Int
    abstract val message: String
    open val data: D? = null

    fun isSuccessful(): Boolean = code == 0
}