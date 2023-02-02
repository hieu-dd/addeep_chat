package net.itanchi.addeep.android.ui.screen.home.friends.search

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.android.util.toViewState

class SearchFriendViewModel : BaseViewModel() {
    private val _searchedFriendViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val searchedFriendViewState: StateFlow<ViewState> = _searchedFriendViewState
    private val _myProfileViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val myProfileViewState: StateFlow<ViewState> = _myProfileViewState

    init {
        scope.launch {
            dataManager.getUser(onlyLocalData = true, onlyRemoteData = false).collectLatest {
                _myProfileViewState.value = it.toViewState()
            }
        }
    }

    fun handleEvent(event: SearchFriendEvent) {
        scope.launch {
            when (event) {
                is SearchFriendEvent.OpenCountryList -> navigationManager.navigate(NavigationDirections.CountryPicker)
                is SearchFriendEvent.Close -> navigationManager.navigate(NavigationDirections.Back)
                is SearchFriendEvent.SearchByPhone -> {
                    event.phone.takeIf { it.isNotBlank() }?.let { searchFriend(phone = it) }
                }
                is SearchFriendEvent.SearchById -> {
                    event.addeepId.takeIf { it.isNotBlank() }?.let { searchFriend(addeepId = it) }
                }
                is SearchFriendEvent.AddFriend -> addFriend(userId = event.friendId)
                is SearchFriendEvent.OpenAddeepId -> navigationManager.navigate(NavigationDirections.AddeepId)
            }
        }
    }

    fun searchFriend(addeepId: String? = null, phone: String? = null) {
        scope.launch {
            dataManager.getUser(
                onlyLocalData = false,
                onlyRemoteData = true,
                addeepId = addeepId,
                phone = phone
            ).collect {
                _searchedFriendViewState.value = when {
                    it.loading -> ViewState.Loading
                    it.exception != null -> ViewState.Error(it.exception)
                    else -> ViewState.Success(it.data)
                }
            }
        }
    }

    fun addFriend(userId: Long) {
        scope.launch {
            dataManager.addContact(userId).collect {
                // TODO
            }
        }
    }
}