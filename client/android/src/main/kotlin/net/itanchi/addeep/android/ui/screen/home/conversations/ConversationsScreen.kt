package net.itanchi.addeep.android.ui.screen.home.conversations

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import net.itanchi.addeep.android.R
import net.itanchi.addeep.android.ui.screen.MainActivity.Companion.getConversationId
import net.itanchi.addeep.android.ui.screen.MainActivity.Companion.removeConversationId
import net.itanchi.addeep.android.ui.screen.common.EmptyPage
import net.itanchi.addeep.android.ui.screen.common.ErrorPage
import net.itanchi.addeep.android.ui.screen.common.LoadingPage
import net.itanchi.addeep.android.util.FlowAsState
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.android.util.findActivity
import net.itanchi.addeep.android.util.humanReadableDate
import net.itanchi.addeep.core.data.model.Conversation
import net.itanchi.addeep.core.data.model.MessageType
import org.koin.androidx.compose.getViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationsScreen(
    homeSavedStateHandle: SavedStateHandle,
    viewModel: ConversationsViewModel = getViewModel(),
) {
    LocalContext.current.findActivity()?.intent?.let { intent ->
        val conversationId = intent.getConversationId().also { intent.removeConversationId() }
        LaunchedEffect(conversationId) {
            if (conversationId > 0) {
                viewModel.handleEvent(ConversationsEvent.OpenConversation(conversationId))
            }
        }
    }

    val conversationsViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.conversationsViewState,
        viewModel.conversationsViewState.value
    )

    val userId = homeSavedStateHandle.get<Long>("userId").also {
        homeSavedStateHandle.set("userId", null)
    }
    val userName = homeSavedStateHandle.get<String>("userName").also {
        homeSavedStateHandle.set("userName", null)
    }

    if (userId != null && userName != null) {
        viewModel.handleEvent(
            ConversationsEvent.OpenConversation(
                userId = userId,
                userName = userName
            )
        )
    }

    ConversationsScreenContent(
        event = viewModel::handleEvent,
        conversationsViewState = conversationsViewState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationsScreenContent(
    event: (ConversationsEvent) -> Unit,
    conversationsViewState: ViewState,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SmallTopAppBar(title = {
                Text(
                    stringResource(R.string.chats_title),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                )
            })
        },
        floatingActionButton = {
            NewChatButton(
                hasConversation = conversationsViewState is ViewState.Success<*> &&
                        (conversationsViewState.data as List<Conversation>).isNotEmpty(),
                onItemClick = {
                    event(ConversationsEvent.OpenContactPicker)
                }
            )
        }
    ) {
        when (conversationsViewState) {
            is ViewState.Loading -> {
                LoadingPage()
            }
            is ViewState.Error -> {
                ErrorPage(
                    conversationsViewState.cause?.message ?: "Unexpected Error"
                )
            }
            is ViewState.Success<*> -> {
                conversationsViewState.data?.let {
                    if ((it as List<Conversation>).isNotEmpty()) {
                        ConversationList(it) { conversation ->
                            event(
                                ConversationsEvent.OpenConversation(
                                    conversation.id
                                )
                            )
                        }
                    } else {
                        EmptyPage("No conversations found")
                    }
                } ?: EmptyPage("No conversations found")
            }
            else -> {}
        }
    }
}

@Composable
fun NewChatButton(
    hasConversation: Boolean,
    onItemClick: () -> Unit
) {
    if (hasConversation) {
        FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            content = {
                Icon(
                    painterResource(R.drawable.ic_bubble_line),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            },
            onClick = onItemClick
        )
    } else {
        ExtendedFloatingActionButton(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            icon = {
                Icon(
                    painterResource(R.drawable.ic_bubble_line),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            },
            text = {
                Text(
                    stringResource(R.string.chats_new_chat),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                )
            },
            onClick = onItemClick,
        )
    }
}

@Composable
fun ConversationList(
    conversations: List<Conversation>,
    onItemClick: (Conversation) -> Unit,
) {
    LazyColumn {
        items(conversations) { conversation ->
            ConversationRow(conversation) { onItemClick(it) }
        }
    }
}

@Composable
fun ConversationRow(
    conversation: Conversation,
    onClick: (Conversation) -> Unit,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .clickable { onClick(conversation) }
            .padding(vertical = 10.dp, horizontal = 16.dp),
    ) {
        val message = conversation.messages.first()

        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(36.dp),
            contentAlignment = Alignment.BottomEnd,
        ) {
            AsyncImage(
                ImageRequest.Builder(context = context)
                    .data(conversation.getDisplayAvatarUrl())
                    .placeholder(R.drawable.placeholder_avatar)
                    .error(R.drawable.placeholder_avatar)
                    .fallback(R.drawable.placeholder_avatar)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 24.dp)
                .weight(1f)
        ) {
            Text(
                text = conversation.getDisplayTitle(),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            val formattedMessage = when (message.type) {
                MessageType.PlainText -> {
                    message.message
                }
                MessageType.Sticker -> {
                    if (message.sender.isMe) stringResource(R.string.chats_message_my_sticker)
                    else stringResource(R.string.chats_message_sticker)
                }
                MessageType.Document,
                MessageType.Gif -> {
                    if (message.sender.isMe) stringResource(R.string.chats_message_my_file)
                    else stringResource(R.string.chats_message_file)
                }
                MessageType.Photo -> {
                    val attachment = message.attachments.last()
                    if (message.sender.isMe)
                        if (attachment.type.startsWith("video")) stringResource(R.string.chats_message_my_video)
                        else stringResource(R.string.chats_message_my_photo)
                    else
                        if (attachment.type.startsWith("video")) stringResource(R.string.chats_message_video)
                        else stringResource(R.string.chats_message_photo)
                }
                else -> {
                    if (message.sender.isMe) stringResource(R.string.chats_message_my_unknown)
                    else stringResource(R.string.chats_message_unknown)
                }
            }
            Text(
                text = formattedMessage,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Column(
            modifier = Modifier.padding(top = 14.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = message.createdAt.humanReadableDate(),
                modifier = Modifier.wrapContentSize(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
        }
    }
}