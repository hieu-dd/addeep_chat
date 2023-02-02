package net.itanchi.addeep.android.ui.screen.home.settings

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.util.ViewState

class SettingsViewModel : BaseViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val _settingViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val settingViewState: StateFlow<ViewState> = _settingViewState

    fun handleEvent(event: SettingsEvent) {
        scope.launch {
            when (event) {
                is SettingsEvent.GoBack -> navigationManager.navigate(NavigationDirections.Back)
                is SettingsEvent.Logout -> {
                    dataManager.logout().collect { dataState ->
                        when {
                            dataState.loading -> {
                                _settingViewState.value = ViewState.Loading
                            }
                            dataState.exception != null -> {
                                _settingViewState.value = ViewState.Error(dataState.exception)
                            }
                            else -> {
                                auth.signOut()
                                navigationManager.navigate(NavigationDirections.Auth)
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
