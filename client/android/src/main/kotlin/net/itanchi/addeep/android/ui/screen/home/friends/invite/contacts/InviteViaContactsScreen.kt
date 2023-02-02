package net.itanchi.addeep.android.ui.screen.home.friends.invite.contacts

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.itanchi.addeep.android.R
import net.itanchi.addeep.android.ui.screen.common.SearchBar
import net.itanchi.addeep.android.ui.screen.home.friends.invite.InviteType
import net.itanchi.addeep.android.util.FlowAsState
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.android.util.sendEmail
import net.itanchi.addeep.android.util.sendSMS
import net.itanchi.addeep.core.data.model.Contact
import net.itanchi.addeep.core.data.model.User
import org.koin.androidx.compose.getViewModel


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun InviteViaContactsScreen(
    inviteType: InviteType,
    viewModel: InviteViaContactsViewModel = getViewModel()
) {
    val localContactsViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.localContactsViewState,
        viewModel.localContactsViewState.value
    )
    val myProfile = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.myProfile,
        viewModel.myProfile.value,
    )
    InviteViaContactsContent(
        inviteType = inviteType,
        localContactsViewState = localContactsViewState,
        myProfile = myProfile,
        event = viewModel::handleEvent
    )
}

@Composable
fun InviteViaContactsContent(
    event: (InviteViaContactEvent) -> Unit,
    inviteType: InviteType,
    localContactsViewState: ViewState,
    myProfile: User?,
) {
    val shareAddeepId =
        myProfile?.addeepId?.let { stringResource(R.string.invite_friend_share_content_addeep_id, it) }.orEmpty()
    val shareContent = stringResource(R.string.invite_friend_share_content, shareAddeepId).trim()

    val context = LocalContext.current

    val localContacts = (localContactsViewState as? ViewState.Success<List<Contact>>)?.data.orEmpty()
    var searchText by remember { mutableStateOf("") }
    val displayContacts = searchContacts(localContacts, searchText, inviteType)
    val selectContacts = remember { mutableStateMapOf<Long, Boolean>() }
    val inviteContacts = displayContacts.filter { selectContacts[it.id] == true }

    val title: String
    val searchHint: String
    val onSend: () -> Unit

    when (inviteType) {
        InviteType.SMS -> {
            title = stringResource(R.string.invite_friend_via_sms)
            searchHint = stringResource(R.string.invite_friend_search_name_or_phone)
            onSend = {
                context.sendSMS(inviteContacts.mapNotNull { it.phones.firstOrNull() }, shareContent)
            }
        }
        else -> {
            title = stringResource(R.string.invite_friend_via_email)
            searchHint = stringResource(R.string.invite_friend_search_email)
            onSend = {
                context.sendEmail(inviteContacts.mapNotNull { it.emails.firstOrNull() }, shareContent)
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                SmallTopAppBar(
                    modifier = Modifier.background(MaterialTheme.colorScheme.onPrimary),
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = { event(InviteViaContactEvent.Back) }) {
                            Icon(painterResource(R.drawable.ic_chevron_left), contentDescription = null)
                        }
                    },
                    actions = {
                        BadgeBox(
                            badgeNumber = inviteContacts.size,
                            icon = painterResource(id = R.drawable.ic_send_line)
                        ) {
                            onSend()
                        }
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                SearchBar(
                    modifier = Modifier
                        .padding(vertical = 6.dp, horizontal = 12.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04F)),
                    searchText = searchText,
                    hint = searchHint,
                    onSearch = {
                        searchText = it
                    },
                )
                ContactList(contacts = displayContacts, inviteType = inviteType, selectContacts = selectContacts) {
                    selectContacts.apply {
                        this[it.id] = !(this[it.id] ?: false)
                    }
                }
            }
        }
    }
}

@Composable
fun ContactList(
    contacts: List<Contact>,
    inviteType: InviteType,
    selectContacts: Map<Long, Boolean>,
    onItemClick: (Contact) -> Unit,
) {
    LazyColumn {
        items(contacts) { contact ->
            val selected = selectContacts[contact.id] ?: false
            ContactRow(contact, inviteType, selected) {
                onItemClick(it)
            }
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
fun ContactRow(
    contact: Contact,
    inviteType: InviteType,
    checked: Boolean,
    onItemClick: (Contact) -> Unit,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick(contact)
            }
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.weight(1F)) {
            Text(
                contact.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                if (inviteType == InviteType.SMS) {
                    contact.phones.firstOrNull().orEmpty()
                } else {
                    contact.emails.firstOrNull().orEmpty()
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6F)
            )
        }
        ContactCheckBox(
            checked = checked
        )
    }
}

@Composable
private fun ContactCheckBox(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
) {
    val checkboxId = if (checked) R.drawable.ic_checkbox_checked else R.drawable.ic_checkbox_unchecked
    val checkboxColor =
        if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    Icon(
        painterResource(checkboxId),
        contentDescription = null,
        modifier = modifier.size(24.dp),
        tint = checkboxColor
    )
}

@Composable
fun BadgeBox(
    badgeNumber: Int,
    icon: Painter,
    onClick: () -> Unit,
) {
    Box {
        if (badgeNumber > 0) {
            Text(
                badgeNumber.toString(),
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
        IconButton(
            onClick = onClick,
            enabled = badgeNumber > 0,
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
            )
        }
    }
}

private fun searchContacts(contacts: List<Contact>, searchText: String, inviteType: InviteType) =
    contacts.filter { contact ->
        (contact.name.contains(other = searchText, ignoreCase = true) ||
                contact.phones.any { it.contains(other = searchText, ignoreCase = true) } ||
                contact.emails.any { it.contains(other = searchText, ignoreCase = true) }) &&
                if (inviteType == InviteType.SMS) {
                    contact.phones.isNotEmpty()
                } else {
                    contact.emails.isNotEmpty()
                }
    }

