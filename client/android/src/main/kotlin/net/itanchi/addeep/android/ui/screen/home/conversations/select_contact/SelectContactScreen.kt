package net.itanchi.addeep.android.ui.screen.home.conversations.select_contact

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.itanchi.addeep.android.R
import net.itanchi.addeep.android.ui.screen.common.EmptyPage
import net.itanchi.addeep.android.ui.screen.common.ErrorPage
import net.itanchi.addeep.android.ui.screen.common.LoadingPage
import net.itanchi.addeep.android.ui.screen.common.SearchBar
import net.itanchi.addeep.android.util.FlowAsState
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.core.data.model.User
import org.koin.androidx.compose.getViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectContactScreen(
    viewModel: SelectContactViewModel = getViewModel(),
) {
    val contactsViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.contactsViewState,
        viewModel.contactsViewState.value
    )
    SelectContactScreenContent(
        event = viewModel::handleEvent,
        contactsViewState = contactsViewState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectContactScreenContent(
    event: (SelectContactEvent) -> Unit,
    contactsViewState: ViewState,
) {
    var searchText by remember { mutableStateOf("") }
    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                SmallTopAppBar(
                    title = { Text(stringResource(R.string.select_contact_title)) },
                    navigationIcon = {
                        IconButton(onClick = { event(SelectContactEvent.Close) }) {
                            Icon(
                                painterResource(R.drawable.ic_close_line),
                                contentDescription = null
                            )
                        }
                    }
                )
            },
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    searchText = searchText,
                    hint = stringResource(R.string.common_search),
                    onSearch = { searchText = it }
                )
                when (contactsViewState) {
                    is ViewState.Loading -> {
                        LoadingPage()
                    }
                    is ViewState.Error -> {
                        ErrorPage(
                            contactsViewState.cause?.message
                                ?: stringResource(id = R.string.common_unexpected_error)
                        )
                    }
                    is ViewState.Success<*> -> {
                        contactsViewState.data?.let {
                            if ((it as? List<User>)?.isNotEmpty() == true) {
                                ContactList(it.filter { contact ->
                                    with(searchText.trim()) {
                                        contact.name.contains(this, true)
                                    }
                                }) { contact ->
                                    event(
                                        SelectContactEvent.SelectContact(contact)
                                    )
                                }
                            } else {
                                EmptyPage(stringResource(R.string.select_contact_not_found))
                            }
                        } ?: EmptyPage(stringResource(R.string.select_contact_not_found))
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun ContactList(
    contacts: List<User>,
    onItemClick: (User) -> Unit,
) {
    LazyColumn {
        items(contacts) { contact ->
            ContactRow(Modifier.padding(horizontal = 16.dp), contact) { onItemClick(it) }
        }
    }
}

@Composable
fun ContactRow(
    modifier: Modifier,
    contact: User,
    onClick: (User) -> Unit,
) {
    Text(
        contact.name,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(contact) }
            .padding(vertical = 12.dp),
        style = MaterialTheme.typography.bodyLarge,
    )
    Divider(
        modifier
            .fillMaxWidth()
    )
}