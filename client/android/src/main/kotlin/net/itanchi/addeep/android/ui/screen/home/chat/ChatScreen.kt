package net.itanchi.addeep.android.ui.screen.home.chat

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.insets.navigationBarsWithImePadding
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.R
import net.itanchi.addeep.android.ui.screen.home.chat.components.*
import net.itanchi.addeep.android.util.*
import net.itanchi.addeep.core.data.model.*
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import net.itanchi.addeep.android.util.currentFraction

val BottomSheetHeightWhenClose = 0.dp
val BottomSheetHeightWhenOpenInCollapseMode = 248.dp

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: ConversationId,
    userId: UserId,
    userName: UserName,
    viewModel: ChatViewModel = getViewModel { parametersOf(conversationId, userId, userName) },
) {
    LaunchedEffect(conversationId) {
        viewModel.handleEvent(ChatEvent.ReloadConversation(conversationId))
        viewModel.handleEvent(ChatEvent.LoadMedia)
    }

    val chatViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.chatViewState,
        viewModel.chatViewState.value
    )

    val gifViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.gifViewState,
        viewModel.gifViewState.value,
    )

    val mediaViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.mediaViewState,
        viewModel.mediaViewState.value,
    )

    ChatScreenContent(
        event = viewModel::handleEvent,
        chatViewState = chatViewState,
        gifViewState = gifViewState,
        mediaViewState = mediaViewState
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ChatScreenContent(
    event: (ChatEvent) -> Unit,
    chatViewState: ViewState,
    gifViewState: ViewState,
    mediaViewState: ViewState
) {
    chatViewState.takeIf { it is ViewState.Success<*> && it.data != null }?.let {
        val conversation = (it as ViewState.Success<*>).data as Conversation
        val gifs = gifViewState.takeIf { gifState -> gifState is ViewState.Success<*> && gifState.data != null }
            ?.let { viewState ->
                (viewState as ViewState.Success<*>).data as List<Gif>
            } ?: listOf()

        val mediaList = mediaViewState.takeIf { photoState -> photoState is ViewState.Success<*> && photoState.data != null }
            ?.let { viewState ->
                (viewState as ViewState.Success<*>).data as List<Pair<Uri, Long?>>
            } ?: listOf()

        val scrollState = rememberLazyListState()
        val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }
        val scope = rememberCoroutineScope()
        var previewMessage by remember { mutableStateOf<Pair<Any, MessageType>?>(null) }

        val context = LocalContext.current

        val scaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
        )

        var gifTabHeight by remember { mutableStateOf(BottomSheetHeightWhenClose) }

        val handleBottomSheetStateOnBackPress: () -> Unit = {
            scope.launch {
                if (scaffoldState.bottomSheetState.isExpanded) {
                    scaffoldState.bottomSheetState.collapse()
                } else if (scaffoldState.bottomSheetState.isCollapsed) {
                    gifTabHeight = BottomSheetHeightWhenClose
                }
            }
        }

        val radius = (20 * scaffoldState.currentFraction).dp

        BottomSheetScaffold(
            modifier = Modifier.fillMaxSize(),
            sheetShape = RoundedCornerShape(topStart = radius, topEnd = radius),
            scaffoldState = scaffoldState,
            backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
            topBar = {
                ChatTopBar(
                    title = conversation.getDisplayTitle(),
                    onBack = { event(ChatEvent.GoBack) }
                )
            },
            sheetContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                        .background(MaterialTheme.colorScheme.onPrimary)
                ) {
                    GifTable(
                        modifier = Modifier.fillMaxSize().fillMaxHeight(0.8f),
                        onGifClick = {
                            scope.launch {
                                if (scaffoldState.bottomSheetState.isExpanded) {
                                    scaffoldState.bottomSheetState.collapse()
                                }
                            }

                            event(ChatEvent.SendMessage(Message(message = it.url, type = MessageType.Gif)))
                        },
                        gifs = gifs,
                        isLoading = gifViewState == ViewState.Loading,
                        onLoadMoreGifs = { searchText -> event(ChatEvent.LoadGifs(searchText, true)) },
                        onExpandedGifList = { isExpanded ->
                            scope.launch {
                                if (isExpanded) {
                                    if (scaffoldState.bottomSheetState.isCollapsed) {
                                        scaffoldState.bottomSheetState.expand()
                                    }
                                }
                            }
                        },
                        onSearch = { searchText ->
                            event(ChatEvent.LoadGifs(searchText, false))
                        },
                        isDisplaySearchBar = !scaffoldState.bottomSheetState.isCollapsed,
                        currentFraction = scaffoldState.currentFraction
                    )
                }
            },
            sheetPeekHeight = gifTabHeight,
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    MessageList(
                        conversationId = conversation.id,
                        messages = conversation.messages,
                        loadMoreMessages = { event(ChatEvent.LoadMoreMessages) },
                        navigateToProfile = { user -> event(ChatEvent.NavigateToProfile(user)) },
                        modifier = Modifier.fillMaxSize(),
                        scrollState = scrollState,
                        onMessageClick = { event(ChatEvent.ClickMessage(it)) }
                    )
                    previewMessage?.let { preview ->
                        MessagePreview(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(128.dp),
                            preview = preview,
                            onPreviewClosed = { previewMessage = null }
                        )
                    }
                }
                ChatInput(
                    onMessageSent = { message, type, attachments ->
                        previewMessage?.let { preview ->
                            when (preview.second) {
                                MessageType.Sticker -> {
                                    val sticker = preview.first as Sticker
                                    event(
                                        ChatEvent.SendMessage(
                                            message = Message(
                                                message = sticker.message,
                                                stickerUrl = sticker.imageFile,
                                                type = MessageType.Sticker,
                                            )
                                        )
                                    )
                                }
                                else -> {}
                            }
                            previewMessage = null
                        } ?: let {
                            val sendMessage = when (type) {
                                MessageType.Gif,
                                MessageType.PlainText -> {
                                    if (message.isNotBlank()) {
                                        Message(
                                            message = message.trim(),
                                            type = type,
                                        )
                                    } else null
                                }
                                MessageType.Photo,
                                MessageType.Document -> {
                                    Message(
                                        message = message.trim(),
                                        type = type,
                                        attachments = attachments.map {
                                            val fileInfo = context.loadFileInfo(it)
                                            MessageAttachment(
                                                name = "",
                                                originalName = fileInfo.name,
                                                type = fileInfo.contentType,
                                                size = fileInfo.contents.size.toLong(),
                                                contents = fileInfo.contents,
                                                localPath = fileInfo.path.toString(),
                                            )
                                        },
                                    )
                                }
                                else -> null
                            }
                            sendMessage?.let { event(ChatEvent.SendMessage(it)) }
                        }
                    },
                    onMessagePreview = { message, type ->
                        previewMessage = Pair(message, type)
                    },
                    modifier = Modifier.navigationBarsWithImePadding(),
                    shouldShowSendButton = previewMessage != null,
                    resetScroll = { scope.launch { scrollState.scrollToItem(0) } },
                    mediaList = mediaList,
                    loadMedia = { event(ChatEvent.LoadMedia) },
                    openPhoneSetting = { event(ChatEvent.OpenPhoneSettings) },
                    onGifTabSelected = { isGifTabSelected ->
                        gifTabHeight =
                            if (isGifTabSelected)
                                BottomSheetHeightWhenOpenInCollapseMode
                            else
                                BottomSheetHeightWhenClose
                    },
                    onHandleBottomSheetBackPress = handleBottomSheetStateOnBackPress,
                    isGifsListExpanding = scaffoldState.bottomSheetState.isExpanded
                )
            }
        }
    }
}

@Composable
private fun MessageList(
    conversationId: Long,
    messages: List<Message>,
    loadMoreMessages: () -> Unit,
    navigateToProfile: (User) -> Unit,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
    onMessageClick: (Message) -> Unit,
) {
    val scope = rememberCoroutineScope()

    Box(modifier = modifier) {
        LazyColumn(
            reverseLayout = true,
            state = scrollState,
            modifier = Modifier.fillMaxSize()
        ) {
            for (index in messages.indices) {
                val prevMessage = messages.getOrNull(index - 1)
                val prevSender = prevMessage?.sender
                val nextMessage = messages.getOrNull(index + 1)
                val nextSender = nextMessage?.sender
                val message = messages[index]
                val sender = message.sender
                val isFirstMessageBySender = prevSender?.id != sender.id
                val isLastMessageBySender = nextSender?.id != sender.id

                val sameDateWithNextMessage =
                    nextMessage != null && message.createdAt.isSameDate(nextMessage.createdAt)
                val sameDateWithPreviousMessage =
                    prevMessage != null && message.createdAt.isSameDate(prevMessage.createdAt)
                val isFirstMessageByAuthorSameDay = isFirstMessageBySender
                        || sameDateWithNextMessage && !sameDateWithPreviousMessage
                        || nextMessage == null
                val isLastMessageByAuthorSameDay = isLastMessageBySender
                        || !sameDateWithNextMessage && (sameDateWithPreviousMessage || prevMessage == null)

                if (prevMessage != null && !message.createdAt.isSameDate(prevMessage.createdAt)) {
                    item {
                        DayHeader(prevMessage.createdAt.chatMessageHeaderDate(context = LocalContext.current))
                    }
                }

                item {
                    MessageRow(
                        conversationId = conversationId,
                        message = message,
                        isFirstMessageByAuthorSameDay = isFirstMessageByAuthorSameDay,
                        isLastMessageByAuthorSameDay = isLastMessageByAuthorSameDay,
                        onMessageClick = onMessageClick,
                        onSenderClick = { user -> navigateToProfile(user) },
                    )
                }

                if (nextMessage == null) {
                    item {
                        DayHeader(message.createdAt.chatMessageHeaderDate(context = LocalContext.current))
                    }
                }
            }
        }

        InfiniteListHandler(scrollState, onLoadMore = loadMoreMessages)

        val jumpThreshold = with(LocalDensity.current) { JumpToBottomThreshold.toPx() }
        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex != 0 || scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }

        JumpToBottom(
            enabled = jumpToBottomButtonEnabled,
            onClicked = { scope.launch { scrollState.animateScrollToItem(0) } },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun DayHeader(dayString: String) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.38F),
                    shape = CircleShape
                )
        ) {
            Text(
                text = dayString,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f),
                )
            )
        }
    }
}

@Preview
@Composable
private fun DayHeaderPreview() {
    DayHeader("Friday, 17 December, 2021")
}

@Composable
private fun MessageRow(
    conversationId: Long,
    message: Message,
    isFirstMessageByAuthorSameDay: Boolean,
    isLastMessageByAuthorSameDay: Boolean,
    onMessageClick: (Message) -> Unit,
    onSenderClick: (User) -> Unit,
) {
    val context = LocalContext.current
    val spaceBetweenAuthors =
        if (isLastMessageByAuthorSameDay) Modifier.padding(top = 8.dp) else Modifier

    Row(modifier = spaceBetweenAuthors) {
        if (isLastMessageByAuthorSameDay && !message.sender.isMe) {
            // Avatar
            Box(
                modifier = Modifier
                    .padding(start = 8.dp, end = 4.dp)
                    .size(32.dp),
                contentAlignment = Alignment.BottomEnd,
            ) {
                AsyncImage(
                    ImageRequest.Builder(context = context)
                        .data(message.sender.getAvatarUrl())
                        .placeholder(R.drawable.placeholder_avatar)
                        .error(R.drawable.placeholder_avatar)
                        .fallback(R.drawable.placeholder_avatar)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(13.dp))
                        .clickable { onSenderClick(message.sender) },
                    contentScale = ContentScale.Crop,
                )
            }
        } else {
            // Space under avatar
            Spacer(modifier = Modifier.width(44.dp))
        }
        AuthorAndTextMessage(
            conversationId = conversationId,
            message = message,
            isFirstMessageByAuthorSameDay = isFirstMessageByAuthorSameDay,
            isLastMessageByAuthorSameDay = isLastMessageByAuthorSameDay,
            modifier = Modifier
                .padding(end = 16.dp)
                .weight(1f),
            onMessageClick = onMessageClick,
        )
    }
}

@Composable
private fun AuthorAndTextMessage(
    conversationId: Long,
    message: Message,
    isFirstMessageByAuthorSameDay: Boolean,
    isLastMessageByAuthorSameDay: Boolean,
    modifier: Modifier = Modifier,
    onMessageClick: (Message) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (message.sender.isMe) Alignment.End else Alignment.Start
    ) {
        val configuration = LocalConfiguration.current
        val maxChatItemWidth = (configuration.screenWidthDp * 0.7).dp
        val maxChatItemHeight = (configuration.screenHeightDp * 0.5).dp
        if (isLastMessageByAuthorSameDay && !message.sender.isMe) {
            Spacer(modifier = Modifier.height(12.dp))
        }
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            if (message.sender.isMe) {
                Spacer(modifier = Modifier.weight(1F))
                MessageTimestamp(timestamp = message.createdAt.chatMessageBubbleTime())
            }

            Box(modifier = Modifier.widthIn(0.dp, maxChatItemWidth).heightIn(0.dp, maxChatItemHeight)) {
                ChatItemBubble(
                    conversationId = conversationId,
                    message = message,
                    isFirstMessageByAuthorSameDay = isFirstMessageByAuthorSameDay,
                    isLastMessageByAuthorSameDay = isLastMessageByAuthorSameDay,
                    onMessageClick = onMessageClick,
                )
            }
            if (!message.sender.isMe) {
                MessageTimestamp(timestamp = message.createdAt.chatMessageBubbleTime())
                Spacer(modifier = Modifier.weight(1F))
            }
        }
        if (isFirstMessageByAuthorSameDay) {
            Spacer(modifier = Modifier.height(12.dp))
        } else {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun MessageTimestamp(timestamp: String) {
    Text(
        timestamp,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6F),
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp)
    )
}

object MessageChatBubbleShape {
    private val TOP_MESSAGE = RoundedCornerShape(18.dp, 18.dp, 18.dp, 6.dp)
    private val CENTER_MESSAGE = RoundedCornerShape(6.dp, 18.dp, 18.dp, 6.dp)
    private val BOTTOM_MESSAGE = RoundedCornerShape(6.dp, 18.dp, 18.dp, 18.dp)

    private val SINGLE_MESSAGE = RoundedCornerShape(18.dp)

    private val TOP_MY_MESSAGE = RoundedCornerShape(18.dp, 18.dp, 6.dp, 18.dp)
    private val CENTER_MY_MESSAGE = RoundedCornerShape(18.dp, 6.dp, 6.dp, 18.dp)
    private val BOTTOM_MY_MESSAGE = RoundedCornerShape(18.dp, 6.dp, 18.dp, 18.dp)

    fun getChatBubbleShape(
        isFirstMessage: Boolean,
        isLastMessage: Boolean,
        isMe: Boolean
    ): RoundedCornerShape {
        return if (isMe) {
            getChatBubbleShapeOfMine(isFirstMessage, isLastMessage)
        } else {
            getChatBubbleShapeOfPartner(isFirstMessage, isLastMessage)
        }
    }

    private fun getChatBubbleShapeOfPartner(
        isFirstMessage: Boolean,
        isLastMessage: Boolean
    ): RoundedCornerShape {
        return when {
            isFirstMessage && isLastMessage -> SINGLE_MESSAGE
            !isFirstMessage && !isLastMessage -> CENTER_MESSAGE
            isFirstMessage -> BOTTOM_MESSAGE
            else -> TOP_MESSAGE
        }
    }

    private fun getChatBubbleShapeOfMine(
        isFirstMessage: Boolean,
        isLastMessage: Boolean
    ): RoundedCornerShape {
        return when {
            isFirstMessage && isLastMessage -> SINGLE_MESSAGE
            !isFirstMessage && !isLastMessage -> CENTER_MY_MESSAGE
            isFirstMessage -> BOTTOM_MY_MESSAGE
            else -> TOP_MY_MESSAGE
        }
    }
}

@Composable
private fun ChatItemBubble(
    conversationId: Long,
    message: Message,
    isFirstMessageByAuthorSameDay: Boolean,
    isLastMessageByAuthorSameDay: Boolean,
    onMessageClick: (Message) -> Unit,
) {
    var backgroundBubbleShape: Shape = MessageChatBubbleShape.getChatBubbleShape(
        isFirstMessageByAuthorSameDay,
        isLastMessageByAuthorSameDay,
        message.sender.isMe
    )
    var backgroundBubbleColor: Color = MaterialTheme.colorScheme.surfaceVariant
    if (message.sender.isMe) {
        backgroundBubbleColor = MaterialTheme.colorScheme.primary
    }
    when (message.type) {
        MessageType.Document -> {
            backgroundBubbleShape = RoundedCornerShape(18.dp)
            if (message.sender.isMe) {
                backgroundBubbleColor = MaterialTheme.colorScheme.primaryContainer
            }
        }
        MessageType.Gif,
        MessageType.Sticker,
        MessageType.Photo -> {
            backgroundBubbleShape = RectangleShape
            backgroundBubbleColor = Color.Transparent
        }
        else -> {}
    }

    Column {
        Surface(
            color = backgroundBubbleColor,
            shape = backgroundBubbleShape,
        ) {
            ClickableMessage(
                conversationId = conversationId,
                message = message,
                onMessageClick = onMessageClick,
            )
        }
    }
}