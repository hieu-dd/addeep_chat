package net.itanchi.addeep.android.ui.screen.home.conversations.select_contact

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.data.ContactsLoader
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.util.ViewState
import org.koin.core.component.inject

class SelectContactViewModel : BaseViewModel() {
    private val contactsLoader: ContactsLoader by inject()

    private val _contactsViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val contactsViewState: StateFlow<ViewState> = _contactsViewState

    init {
        fetchContacts()
    }

    fun handleEvent(event: SelectContactEvent) {
        scope.launch {
            when (event) {
                is SelectContactEvent.Close -> navigationManager.navigate(NavigationDirections.Back)
                is SelectContactEvent.SelectContact -> {
                    navigationManager.navigate(
                        NavigationDirections.FinishWithResults(
                            mapOf(
                                "userId" to event.contact.id,
                                "userName" to event.contact.name
                            )
                        )
                    )
                }
            }
        }
    }

    private fun fetchContacts() {
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