package net.itanchi.addeep.android.ui.screen.home.friends.invite

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.android.util.toViewState

class InviteFriendsViewModel : BaseViewModel() {
    private val _myProfileViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val myProfileViewState: StateFlow<ViewState> = _myProfileViewState

    init {
        scope.launch {
            dataManager.getUser(onlyLocalData = true, onlyRemoteData = false).collect {
                _myProfileViewState.value = it.toViewState()
            }
        }
    }

    fun handleEvent(event: InviteFriendsEvent) {
        scope.launch {
            when (event) {
                is InviteFriendsEvent.Back -> navigationManager.navigate(NavigationDirections.Back)
                is InviteFriendsEvent.InviteViaContact -> navigationManager.navigate(
                    NavigationDirections.InviteViaContacts(
                        event.inviteType
                    )
                )
            }
        }
    }
}