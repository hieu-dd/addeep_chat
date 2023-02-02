package net.itanchi.addeep.android.ui.screen.home.contacts

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.data.ContactsLoader
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.util.ViewState
import org.koin.core.component.inject

class ContactsViewModel : BaseViewModel() {
    private val contactsLoader: ContactsLoader by inject()
    private val _contactsViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)

    val contactsViewState: StateFlow<ViewState> = _contactsViewState

    init {
        syncContacts()
    }

    fun handleEvent(event: ContactsEvent) {
        scope.launch {
            when (event) {
                is ContactsEvent.OpenPhoneSettings -> navigationManager.navigate(NavigationDirections.PhoneSettings)
                is ContactsEvent.SyncContacts -> syncContacts()
                is ContactsEvent.Chat -> {
                    navigationManager.navigate(
                        NavigationDirections.Chat(
                            conversationId = 0,
                            userId = event.contact.id,
                            userName = event.contact.name
                        )
                    )
                }
                is ContactsEvent.SearchFriend -> {
                    navigationManager.navigate(NavigationDirections.SearchFriend)
                }
                is ContactsEvent.InviteFriend -> {
                    navigationManager.navigate(
                        NavigationDirections.InviteFriend
                    )
                }
            }
        }
    }

    private fun syncContacts() {
        scope.launch {
            dataManager.syncUserContacts(listOf()).collect { dataState ->
                when {
                    dataState.loading -> {
                        _contactsViewState.value = ViewState.Loading
                    }
                    dataState.exception != null -> {
                        _contactsViewState.value = ViewState.Error(dataState.exception)
                    }
                    else -> {
                        _contactsViewState.value = ViewState.Success(dataState.data)
                        val localContacts = contactsLoader.loadContacts().toList()
                        dataManager.syncUserContacts(localContacts).collect {
                            it.takeIf { !it.data.isNullOrEmpty() }?.let {
                                _contactsViewState.value = ViewState.Success(it.data)
                            }
                        }
                    }
                }
            }
        }
    }
}
