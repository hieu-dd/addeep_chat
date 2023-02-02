package net.itanchi.addeep.android.ui.screen.auth.login.countries

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.util.ViewState

class CountriesViewModel : BaseViewModel() {
    private val _countriesViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val countriesViewState: StateFlow<ViewState> = _countriesViewState

    fun handleEvent(event: CountriesEvent) {
        scope.launch {
            val direction = when (event) {
                is CountriesEvent.Close -> NavigationDirections.Back
                is CountriesEvent.SelectCountry -> NavigationDirections.FinishWithResults(
                    mapOf("countryCode" to event.countryCode)
                )

            }
            navigationManager.navigate(direction)
        }
    }
}
