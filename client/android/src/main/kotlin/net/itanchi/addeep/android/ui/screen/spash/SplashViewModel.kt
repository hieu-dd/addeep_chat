package net.itanchi.addeep.android.ui.screen.spash

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.core.data.model.User
import java.util.*

class SplashViewModel : BaseViewModel() {
    private val auth: FirebaseAuth = Firebase.auth.apply { setLanguageCode(Locale.getDefault().language) }

    private val _splashViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val splashViewState: StateFlow<ViewState> = _splashViewState

    init {
        fetchUser()
    }

    fun handleEvent(event: SplashEvent) {
        scope.launch {
            when (event) {
                is SplashEvent.Continue -> {
                    navigationManager.navigate(NavigationDirections.TermsOfService)
                }
            }
        }
    }

    private fun fetchUser() {
        scope.launch {
            _splashViewState.value = ViewState.Loading
            auth.currentUser?.getIdToken(true)?.await()?.token.takeIf { !it.isNullOrBlank() }?.let {
                dataManager.updateAuthToken(it)
                dataManager.getUser(
                    onlyLocalData = false,
                    onlyRemoteData = true,
                ).collect { dataState ->
                    when {
                        dataState.loading -> {
                            _splashViewState.value = ViewState.Loading
                        }
                        dataState.data != null -> {
                            if (!dataState.data?.name.isNullOrBlank()) {
                                navigationManager.navigate(NavigationDirections.Home)
                            } else {
                                navigationManager.navigate(NavigationDirections.AuthRegister)
                            }
                        }
                        else -> {
                            _splashViewState.value = ViewState.Success<User>(data = null)
                        }
                    }
                }
            } ?: let {
                _splashViewState.value = ViewState.Success<User>(data = null)
            }
        }
    }
}
