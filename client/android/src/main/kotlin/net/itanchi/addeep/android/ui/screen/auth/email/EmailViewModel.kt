package net.itanchi.addeep.android.ui.screen.auth.email

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.core.data.model.Preferences

class EmailViewModel : BaseViewModel() {
    private val _emailViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val emailViewState: StateFlow<ViewState> = _emailViewState

    fun handleEvent(event: EmailEvent) {
        scope.launch {
            when (event) {
                is EmailEvent.AddEmail -> updateEmail(event.email, event.preferences)
                is EmailEvent.Skip -> navigationManager.navigate(NavigationDirections.Home)
                is EmailEvent.DismissError -> _emailViewState.value = ViewState.Idle
            }
        }
    }

    private suspend fun updateEmail(
        email: String,
        preferences: Preferences,
    ) {
        dataManager.updateUser(email = email, preferences = preferences).collect { dataState ->
            when {
                dataState.loading -> {
                    _emailViewState.value = ViewState.Loading
                }
                dataState.data != null -> {
                    navigationManager.navigate(NavigationDirections.Home)
                }
                else -> {
                    _emailViewState.value = ViewState.Error(dataState.exception)
                }
            }
        }
    }
}
