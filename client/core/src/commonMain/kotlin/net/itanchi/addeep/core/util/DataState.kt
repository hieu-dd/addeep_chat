package net.itanchi.addeep.core.util

data class DataState<out T>(
    val data: T? = null,
    val exception: Throwable? = null,
    val loading: Boolean = false,
)