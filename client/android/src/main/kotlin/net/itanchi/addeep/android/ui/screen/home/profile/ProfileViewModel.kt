package net.itanchi.addeep.android.ui.screen.home.profile

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.util.ViewState

class ProfileViewModel : BaseViewModel() {
    private val _profileViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val profileViewState: StateFlow<ViewState> = _profileViewState

    init {
        fetchUserProfile()
    }

    fun handleEvent(event: ProfileEvent) {
        scope.launch {
            when (event) {
                is ProfileEvent.GoBack -> navigationManager.navigate(NavigationDirections.Back)
            }
        }
    }

    private fun fetchUserProfile() {
        scope.launch {
            dataManager.getUser(onlyLocalData = true, onlyRemoteData = false).collect { dataState ->
                when {
                    dataState.loading -> {
                        _profileViewState.value = ViewState.Loading
                    }
                    dataState.exception != null -> {
                        _profileViewState.value = ViewState.Error(dataState.exception)
                    }
                    else -> {
                        _profileViewState.value = ViewState.Success(dataState.data)
                    }
                }
            }
        }
    }

}
