package net.itanchi.addeep.android.ui.screen.home.friends.invite.contacts

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.data.ContactsLoader
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.core.data.model.User
import org.koin.core.component.inject

class InviteViaContactsViewModel : BaseViewModel() {
    private val contactsLoader: ContactsLoader by inject()
    private val _localContactsViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val localContactsViewState: StateFlow<ViewState> = _localContactsViewState

    private val _myProfile: MutableStateFlow<User?> = MutableStateFlow(null)
    val myProfile: StateFlow<User?> = _myProfile

    init {
        scope.launch {
            val localContacts = contactsLoader.loadContacts().toList()
            dataManager.syncUserContacts(localContacts).collectLatest { friends ->
                _localContactsViewState.value = ViewState.Success(localContacts.filter { localContact ->
                    friends.data?.none { it.id == localContact.id } ?: true
                })
            }

            dataManager.getUser(onlyLocalData = true, onlyRemoteData = false).collect {
                _myProfile.value = it.data
            }
        }
    }

    fun handleEvent(event: InviteViaContactEvent) {
        scope.launch {
            when (event) {
                is InviteViaContactEvent.Back -> navigationManager.navigate(NavigationDirections.Back)
            }
        }
    }
}