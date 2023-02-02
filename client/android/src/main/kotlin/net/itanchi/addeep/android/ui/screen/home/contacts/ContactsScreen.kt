package net.itanchi.addeep.android.ui.screen.home.contacts

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import net.itanchi.addeep.android.R
import net.itanchi.addeep.android.ui.screen.common.EmptyPage
import net.itanchi.addeep.android.ui.screen.common.ErrorPage
import net.itanchi.addeep.android.ui.screen.common.LoadingPage
import net.itanchi.addeep.android.ui.screen.common.Permission
import net.itanchi.addeep.android.util.FlowAsState
import net.itanchi.addeep.android.util.PermissionChecker
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.core.data.model.User
import org.koin.androidx.compose.getViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ContactsScreen(
    viewModel: ContactsViewModel = getViewModel(),
) {
    val contactsViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.contactsViewState,
        viewModel.contactsViewState.value
    )
    ContactsScreenContent(
        event = viewModel::handleEvent,
        contactsViewState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreenContent(
    event: (ContactsEvent) -> Unit,
    contactsViewState: ViewState,
) {
    val context = LocalContext.current
    val permissionChecker = PermissionChecker(context)

    var showOptionAddFriends by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                SmallTopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.friends_title),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        )
                    },
                    actions = {
                        IconButton(onClick = {
                            showOptionAddFriends = true
                        }) {
                            Icon(painterResource(R.drawable.ic_add_person_line), contentDescription = null)
                        }
                    }
                )
            },
        ) {
            when {
                !permissionChecker.canReadContacts() -> {
                    Permission(
                        modifier = Modifier.fillMaxSize(),
                        permissions = mapOf(
                            android.Manifest.permission.READ_CONTACTS to stringResource(R.string.permission_contacts_description)
                        ),
                        onPermissionAccept = { event(ContactsEvent.SyncContacts) },
                        openPhoneSettings = { event(ContactsEvent.OpenPhoneSettings) }
                    )
                }
                contactsViewState is ViewState.Loading -> {
                    LoadingPage()
                }
                contactsViewState is ViewState.Error -> {
                    ErrorPage(contactsViewState.cause?.message ?: "Unexpected Error")
                }
                contactsViewState is ViewState.Success<*> -> {
                    contactsViewState.data?.let {
                        if ((it as List<User>).isNotEmpty()) {
                            ContactList(
                                contacts = it,
                                onItemClick = { user -> event(ContactsEvent.Chat(user)) },
                                onInvite = { event(ContactsEvent.InviteFriend) },
                            )
                        } else {
                            EmptyPage("No contacts found")
                        }
                    } ?: EmptyPage("No contacts found")
                }
                else -> {}
            }
        }
    }
    if (showOptionAddFriends) {
        AddFriendDialog(
            onScanQr = {
                showOptionAddFriends = false
            },
            onSearch = {
                event(ContactsEvent.SearchFriend)
                showOptionAddFriends = false
            },
            onInvite = {
                event(ContactsEvent.InviteFriend)
                showOptionAddFriends = false
            },
            onDismiss = {
                showOptionAddFriends = false
            }
        )
    }
}

@Composable
fun ContactList(
    contacts: List<User>,
    onItemClick: (User) -> Unit,
    onInvite: () -> Unit,
) {
    LazyColumn {
        items(contacts) { contact ->
            ContactRow(contact) { onItemClick(it) }
            Divider(modifier = Modifier.padding(start = 72.dp))
        }
        item {
            InviteFriendRow(onInvite)
        }
    }
}

@Composable
fun ContactRow(
    contact: User,
    onClick: (User) -> Unit,
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .clickable { onClick(contact) }
            .padding(vertical = 10.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.BottomEnd,
        ) {
            AsyncImage(
                ImageRequest.Builder(context = context)
                    .data(contact.getAvatarUrl())
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

        Text(
            text = contact.name,
            modifier = Modifier
                .padding(start = 24.dp)
                .weight(1F),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
fun InviteFriendRow(
    onInvite: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable {
                onInvite()
            }
            .padding(vertical = 10.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.BottomEnd,
        ) {
            Icon(
                painterResource(id = R.drawable.ic_email_line),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08F))
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        Text(
            text = stringResource(id = R.string.contacts_invite_to_addeep),
            modifier = Modifier
                .padding(start = 24.dp)
                .weight(1F),
            style = MaterialTheme.typography.bodyLarge,
        )
        Icon(painter = painterResource(id = R.drawable.ic_chevron_right), contentDescription = null)
    }
}

@Composable
fun AddFriendDialog(
    modifier: Modifier = Modifier,
    onScanQr: () -> Unit,
    onSearch: () -> Unit,
    onInvite: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(R.string.contacts_add_friend),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                AddFriendRow(
                    title = stringResource(R.string.contacts_qr_code),
                    icon = painterResource(R.drawable.ic_scan_qrcode),
                    onClick = onScanQr
                )
                AddFriendRow(
                    title = stringResource(R.string.common_search),
                    icon = painterResource(R.drawable.ic_search_line),
                    onClick = onSearch
                )
                AddFriendRow(
                    title = stringResource(R.string.contacts_invite),
                    icon = painterResource(R.drawable.ic_email_line),
                    onClick = onInvite
                )
            }
        },
        confirmButton = {},
    )
}

@Composable
fun AddFriendRow(
    modifier: Modifier = Modifier,
    title: String,
    icon: Painter,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(2.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null)
            Text(
                title,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}