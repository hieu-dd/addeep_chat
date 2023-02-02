package net.itanchi.addeep.android.ui.screen.spash.tos

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.util.ViewState

class TermsOfServiceViewModel : BaseViewModel() {
    private val _termsOfServiceViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val termsOfServiceViewState: StateFlow<ViewState> = _termsOfServiceViewState

    fun handleEvent(event: TermsOfServiceEvent) {
        scope.launch {
            when (event) {
                is TermsOfServiceEvent.Continue -> {
                    navigationManager.navigate(NavigationDirections.Auth)
                }
            }
        }
    }
}
