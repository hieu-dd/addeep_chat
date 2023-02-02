package net.itanchi.addeep.android.ui.screen.home.more

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.util.ViewState

class MoreViewModel : BaseViewModel() {
    private val _moreViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val moreViewState: StateFlow<ViewState> = _moreViewState

    fun handleEvent(event: MoreEvent) {
        scope.launch {
            when (event) {
                is MoreEvent.NavigateToSettings -> navigationManager.navigate(NavigationDirections.Settings)
                is MoreEvent.NavigateToProfile -> navigationManager.navigate(NavigationDirections.Profile)
                is MoreEvent.NavigateToPointsHistory -> navigationManager.navigate(NavigationDirections.PointsHistory)
            }
        }
    }

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        scope.launch {
            dataManager.getUser(onlyLocalData = false, onlyRemoteData = false).collect { dataState ->
                when {
                    dataState.loading -> {
                        _moreViewState.value = ViewState.Loading
                    }
                    dataState.exception != null -> {
                        _moreViewState.value = ViewState.Error(dataState.exception)
                    }
                    else -> {
                        _moreViewState.value = ViewState.Success(dataState.data)
                    }
                }
            }
        }
    }
}
