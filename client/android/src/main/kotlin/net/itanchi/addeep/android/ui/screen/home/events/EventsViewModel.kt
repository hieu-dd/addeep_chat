package net.itanchi.addeep.android.ui.screen.home.events

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.util.ViewState

class EventsViewModel : BaseViewModel() {
    private val _eventsViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)

    val eventsViewState: StateFlow<ViewState> = _eventsViewState

    init {
        getEvents()
    }

    fun handleEvent(event: EventsEvent) {
        scope.launch {
            when (event) {
                is EventsEvent.ViewEvent -> {
                    navigationManager.navigate(NavigationDirections.Default)
                }
            }
        }
    }

    private fun getEvents() {
        scope.launch {
            dataManager.getEvents().collect { dataState ->
                when {
                    dataState.loading -> {
                        _eventsViewState.value = ViewState.Loading
                    }
                    dataState.exception != null -> {
                        _eventsViewState.value = ViewState.Error(dataState.exception)
                    }
                    else -> {
                        _eventsViewState.value = ViewState.Success(dataState.data)
                    }
                }
            }
        }
    }
}
