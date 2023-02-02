package net.itanchi.addeep.android.util

import net.itanchi.addeep.core.util.DataState

sealed class ViewState {
    object Idle : ViewState()
    object Loading : ViewState()
    data class Error(var cause: Throwable? = null) : ViewState()
    data class Success<T>(val data: T? = null) : ViewState()
}

fun <T> DataState<T>.toViewState(): ViewState {
    return when {
        loading -> ViewState.Loading
        exception != null -> ViewState.Error(exception)
        else -> ViewState.Success(data)
    }
}

fun ViewState.getOrNull() = if (this is ViewState.Success<*>) {
    this.data
} else {
    null
}
