package net.itanchi.addeep.android.ui.screen.home.conversations

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.util.ViewState

class ConversationsViewModel : BaseViewModel() {
    private val _conversationsViewState: MutableStateFlow<ViewState> =
        MutableStateFlow(ViewState.Idle)
    val conversationsViewState: StateFlow<ViewState> = _conversationsViewState

    init {
        fetchConversations()
    }

    fun handleEvent(event: ConversationsEvent) {
        scope.launch {
            when (event) {
                is ConversationsEvent.OpenConversation -> {
                    navigationManager.navigate(
                        NavigationDirections.Chat(
                            conversationId = event.conversationId,
                            userId = event.userId,
                            userName = event.userName
                        )
                    )
                }
                is ConversationsEvent.OpenContactPicker -> {
                    navigationManager.navigate(NavigationDirections.SelectContact)
                }
            }
        }
    }

    private fun fetchConversations() {
        scope.launch {
            dataManager.getConversations().collect { dataState ->
                when {
                    dataState.loading -> {
                        _conversationsViewState.value = ViewState.Loading
                    }
                    dataState.exception != null -> {
                        _conversationsViewState.value = ViewState.Error(dataState.exception)
                    }
                    else -> {
                        _conversationsViewState.value = ViewState.Success(dataState.data)
                    }
                }
            }
        }
    }
}
