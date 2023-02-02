package net.itanchi.addeep.android.ui.screen.auth.otp

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.util.ViewState

class OTPViewModel : BaseViewModel() {
    private val _otpViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val otpViewState: StateFlow<ViewState> = _otpViewState

    fun handleEvent(event: OTPEvent) {
        scope.launch {
            when (event) {
                is OTPEvent.Login -> login(event.token)
                is OTPEvent.DismissError -> _otpViewState.value = ViewState.Idle
            }
        }
    }

    private suspend fun login(
        token: String,
    ) {
        dataManager.updateAuthToken(token)
        dataManager.getUser(
            onlyLocalData = false,
            onlyRemoteData = true,
        ).collect { dataState ->
            when {
                dataState.loading -> {
                    _otpViewState.value = ViewState.Loading
                }
                dataState.data != null -> {
                    if (!dataState.data?.name.isNullOrBlank()) {
                        navigationManager.navigate(NavigationDirections.Home)
                    } else {
                        navigationManager.navigate(NavigationDirections.AuthRegister)
                    }
                }
                else -> {
                    _otpViewState.value = ViewState.Error(dataState.exception)
                }
            }
        }
    }
}
