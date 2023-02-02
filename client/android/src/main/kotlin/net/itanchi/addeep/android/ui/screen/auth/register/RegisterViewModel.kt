package net.itanchi.addeep.android.ui.screen.auth.register

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.core.data.model.Gender

class RegisterViewModel : BaseViewModel() {
    private val _registerViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val registerViewState: StateFlow<ViewState> = _registerViewState

    fun handleEvent(event: RegisterEvent) {
        scope.launch {
            when (event) {
                is RegisterEvent.Register -> register(event.name, event.dob, event.gender)
                is RegisterEvent.UploadAvatar -> uploadAvatar(event.avatar)
                is RegisterEvent.DismissError -> _registerViewState.value = ViewState.Idle
            }
        }
    }

    private suspend fun register(
        name: String,
        dob: LocalDate?,
        gender: Gender?,
    ) {
        dataManager.updateUser(name = name, dob = dob, gender = gender).collect { dataState ->
            when {
                dataState.loading -> {
                    _registerViewState.value = ViewState.Loading
                }
                dataState.data != null -> {
                    navigationManager.navigate(NavigationDirections.AuthEmail)
                }
                else -> {
                    _registerViewState.value = ViewState.Error(dataState.exception)
                }
            }
        }
    }

    private suspend fun uploadAvatar(
        avatar: ByteArray
    ) {
        dataManager.uploadUserAvatar(avatar).collect {

        }
    }
}
