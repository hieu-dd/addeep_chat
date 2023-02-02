package net.itanchi.addeep.android.ui.screen.auth.login

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.util.ViewState

class LoginViewModel : BaseViewModel() {
    private val _phoneViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val loginViewState: StateFlow<ViewState> = _phoneViewState

    fun handleEvent(event: LoginEvent) {
        scope.launch {
            when (event) {
                is LoginEvent.OpenCountryList -> {
                    navigationManager.navigate(NavigationDirections.CountryPicker)
                }
                is LoginEvent.SendOTP -> {
                    navigationManager.navigate(NavigationDirections.AuthOTP(event.phone))
                }
            }
        }
    }
}
