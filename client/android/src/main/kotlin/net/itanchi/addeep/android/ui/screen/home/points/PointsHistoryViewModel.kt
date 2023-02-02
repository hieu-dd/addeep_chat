package net.itanchi.addeep.android.ui.screen.home.points

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.core.data.model.Point

class PointsHistoryViewModel : BaseViewModel() {
    private val pageSize = 20
    private val points = mutableListOf<Point>()
    private var noMorePoints = false
    private val _pointsHistoryViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)

    val pointsHistoryViewState: StateFlow<ViewState> = _pointsHistoryViewState

    init {
        getPointsHistory()
    }

    fun handleEvent(event: PointsHistoryEvent) {
        scope.launch {
            when (event) {
                is PointsHistoryEvent.Close -> navigationManager.navigate(NavigationDirections.Back)
                is PointsHistoryEvent.LoadMoreHistory -> {
                    getPointsHistory()
                }
            }
        }
    }

    private fun getPointsHistory() {
        scope.launch {
            if (noMorePoints) return@launch

            dataManager.getPointsHistory(points.size / pageSize + 1, pageSize).collect { dataState ->
                when {
                    dataState.loading -> {
                        _pointsHistoryViewState.value = ViewState.Loading
                    }
                    dataState.exception != null -> {
                        _pointsHistoryViewState.value = ViewState.Error(dataState.exception)
                    }
                    else -> {
                        dataState.data?.let {
                            points.addAll(it)
                            if (it.size < pageSize) noMorePoints = true
                        }
                        _pointsHistoryViewState.value = ViewState.Success(points)
                    }
                }
            }
        }
    }
}
