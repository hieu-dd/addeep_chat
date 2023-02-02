package net.itanchi.addeep.android.ui.screen.home.chat

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.data.MediaLoader
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.core.data.model.*
import org.koin.core.component.inject

@OptIn(FlowPreview::class)
class ChatViewModel(
    private var conversationId: ConversationId,
    private var userId: UserId,
    private var userName: UserName
) : BaseViewModel() {
    private val mediaLoader: MediaLoader by inject()
    private var job: Job? = null
    private var noMoreMessages: Boolean = false
    private val _chatViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val chatViewState: StateFlow<ViewState> = _chatViewState

    private val _searchGifText =  MutableStateFlow("")
    private val pageSize = 10
    private val gifs = mutableListOf<Gif>()
    private var noMoreGifs = false
    private val _gifViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val gifViewState: StateFlow<ViewState> = _gifViewState

    private val _mediaViewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val mediaViewState: StateFlow<ViewState> = _mediaViewState

    init {
        fetchConversation(onlyLocalData = false)
        viewModelScope.launch {
            _searchGifText.debounce(300).collect {
                gifs.clear()
                noMoreGifs = false
                getGifs(it)
            }
        }
    }

    fun handleEvent(event: ChatEvent) {
        scope.launch {
            when (event) {
                is ChatEvent.GoBack -> navigationManager.navigate(NavigationDirections.Back)
                is ChatEvent.SendMessage -> {
                    dataManager.sendConversationMessage(
                        conversationId = conversationId.value,
                        userId = userId.value,
                        message = event.message,
                    ).takeIf { it > 0L && conversationId.value == 0L }?.let {
                        conversationId = it.toConversationId()
                        fetchConversation(onlyLocalData = true)
                    }
                }
                is ChatEvent.ClickMessage -> {
                    when (event.message.type) {
                        MessageType.Sticker -> viewStickerMessage(event.message)
                        MessageType.Document -> viewDocumentMessage(event.message)
                        MessageType.Photo -> viewPhotoMessage(event.message)
                        else -> {}
                    }
                }
                is ChatEvent.LoadMoreMessages -> {
                    if (noMoreMessages) return@launch

                    chatViewState.value.takeIf { it is ViewState.Success<*> && it.data != null }?.let {
                        val conversation = (it as ViewState.Success<*>).data as Conversation
                        val oldestMessageId = conversation.messages.lastOrNull()?.id

                        dataManager.getConversationMessages(
                            conversationId = conversationId.value,
                            messageBefore = oldestMessageId,
                            messageAfter = null,
                        ).takeIf { it.isNotEmpty() }
                            ?.let { fetchConversation(onlyLocalData = true) }
                            ?: let { noMoreMessages = true }
                    }
                }
                is ChatEvent.ReloadConversation -> {
                    if (event.conversationId == conversationId || event.conversationId == 0L.toConversationId()) return@launch

                    noMoreMessages = false
                    conversationId = event.conversationId
                    userId = 0L.toUserId()
                    userName = "".toUserName()
                    fetchConversation(onlyLocalData = false)
                }
                is ChatEvent.LoadGifs -> {
                    if (event.isLoadMore) {
                        getGifs(event.search)
                    } else {
                        _searchGifText.value = event.search
                    }
                }
                is ChatEvent.OpenPhoneSettings -> navigationManager.navigate(
                    NavigationDirections.PhoneSettings
                )
                is ChatEvent.LoadMedia -> loadMedia()
                else -> {}
            }
        }
    }

    private suspend fun viewStickerMessage(message: Message) {
        dataManager.viewConversationMessage(
            conversationId = conversationId.value,
            message = message,
        ).firstOrNull()?.let {
            navigationManager.navigate(NavigationDirections.WebView(it.url))
        }
    }

    private suspend fun viewDocumentMessage(message: Message) {
        val attachment = message.attachments.first()
        val remoteKey = "conversations/${conversationId.value}/messages/${message.id}/${attachment.name}"
        mediaLoader.loadDocument(
            localPath = attachment.localPath,
            remoteKey = remoteKey
        ).takeIf { it.isNotBlank() }?.let {
            navigationManager.navigate(NavigationDirections.FileViewer(it))
        }
    }

    private suspend fun viewPhotoMessage(message: Message) {
        val attachment = message.attachments.first()
        val localKey = "${message.localId}/${attachment.originalName}"
        val remoteKey = "conversations/${conversationId.value}/messages/${message.id}/${attachment.name}"
        mediaLoader.loadPhoto(
            localPath = attachment.localPath,
            localKey = localKey,
            remoteKey = remoteKey,
        ).takeIf { it.isNotBlank() }?.let {
            navigationManager.navigate(NavigationDirections.FileViewer(it))
        }
    }

    private fun fetchConversation(onlyLocalData: Boolean) {
        job?.cancel()
        job = scope.launch {
            if (conversationId.value == 0L) {
                dataManager.findConversationWithUser(userId.value)?.let {
                    conversationId = it.id.toConversationId()
                }
            }
            if (conversationId.value != 0L) {
                dataManager.latestConversationId = conversationId.value
                dataManager.getConversation(conversationId.value, onlyLocalData).collect { dataState ->
                    when {
                        dataState.loading -> {
                            if (!onlyLocalData) _chatViewState.value = ViewState.Loading
                        }
                        dataState.exception != null -> {
                            if (!onlyLocalData) _chatViewState.value = ViewState.Error(dataState.exception)
                        }
                        else -> {
                            _chatViewState.value = ViewState.Success(dataState.data)
                        }
                    }
                }
            } else {
                _chatViewState.value = ViewState.Success(createEmptyConversation())
            }
        }
    }

    private fun createEmptyConversation(): Conversation {
        return Conversation(
            id = 0,
            title = "",
            type = ConversationType.Single,
            creator = User(),
            participants = listOf(User(name = userName.value)),
        )
    }

    override fun onCleared() {
        dataManager.latestConversationId = 0
        super.onCleared()
    }

    private fun getGifs(search: String) {
        scope.launch {
            if (noMoreGifs) return@launch

            dataManager.getGifs(search, gifs.size / pageSize + 1, pageSize).collect { dataState ->
                when {
                    dataState.loading -> {
                        _gifViewState.value = ViewState.Loading
                    }
                    dataState.exception != null -> {
                        _gifViewState.value = ViewState.Error(dataState.exception)
                    }
                    else -> {
                        dataState.data?.let {
                            gifs.addAll(it)
                            if (it.size < pageSize) noMoreGifs = true
                        }
                        _gifViewState.value = ViewState.Success(gifs)
                    }
                }
            }
        }
    }

    private fun loadMedia() {
        scope.launch {
            val localImages =
                mediaLoader.loadMedia().toList()
            _mediaViewState.value = ViewState.Success(localImages)
        }
    }
}
