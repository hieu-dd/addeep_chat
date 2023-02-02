package net.itanchi.addeep.android.ui.screen.home.createId

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.util.ViewState

class AddeepIdViewModel : BaseViewModel() {
    private val _myProfileViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val myProfileViewState: StateFlow<ViewState> = _myProfileViewState

    init {
        scope.launch {
            dataManager.getUser(onlyLocalData = true, onlyRemoteData = false).collect {
                when {
                    it.loading -> {}
                    it.exception != null -> _myProfileViewState.value = ViewState.Error(it.exception)
                    else -> {
                        it.data.let { user ->
                            _myProfileViewState.value = ViewState.Success(user)
                        }
                    }
                }
            }
        }
    }

    fun handleEvent(event: AddeepIdEvent) {
        scope.launch {
            when (event) {
                is AddeepIdEvent.Back -> navigationManager.navigate(NavigationDirections.Back)
                is AddeepIdEvent.RegisterAddeepId -> {
                    event.addeepId.takeIf { it.isNotBlank() }?.let { updateUser(addeepId = it) }
                }
                is AddeepIdEvent.ToggleSearchableAddeepId -> {
                    updateUser(searchable = event.enable)
                }
            }
        }
    }

    private suspend fun updateUser(addeepId: String? = null, searchable: Boolean? = null) {
        scope.launch {
            dataManager.updateUser(addeepId = addeepId, allowToSearchByAddeepId = searchable).collect {
                when {
                    it.loading -> {}
                    it.exception != null -> _myProfileViewState.value = ViewState.Error(it.exception)
                    else -> {
                        if (!addeepId.isNullOrBlank()) {
                            navigationManager.navigate(NavigationDirections.Back)
                        }
                        _myProfileViewState.value = ViewState.Success(it.data)
                    }
                }
            }
        }
    }
}