package net.itanchi.addeep.android.ui.screen.home.friends.search

import android.annotation.SuppressLint
import android.telephony.PhoneNumberUtils.formatNumberToE164
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.SavedStateHandle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.R
import net.itanchi.addeep.android.data.Countries
import net.itanchi.addeep.android.ui.screen.common.SearchBar
import net.itanchi.addeep.android.util.FlowAsState
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.core.data.model.User
import org.koin.androidx.compose.getViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun SearchFriendScreen(
    savedStateHandle: SavedStateHandle,
    viewModel: SearchFriendViewModel = getViewModel(),
) {
    val countryCode = savedStateHandle.get<String>("countryCode")
    val searchedFriendViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.searchedFriendViewState,
        viewModel.searchedFriendViewState.value
    )
    val myProfileViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.myProfileViewState,
        viewModel.myProfileViewState.value
    )
    SearchFriendContent(viewModel::handleEvent, countryCode, myProfileViewState, searchedFriendViewState)
}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchFriendContent(
    event: (SearchFriendEvent) -> Unit,
    countryCode: String?,
    myProfileViewState: ViewState,
    searchedFriend: ViewState,
) {
    val tabTitles = listOf(
        stringResource(R.string.search_friend_tab_id),
        stringResource(R.string.search_friend_tab_phone_number),
    )
    val myProfile = (myProfileViewState as? ViewState.Success<User>)?.data
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = { Text(stringResource(R.string.search_friend_title)) },
                    navigationIcon = {
                        IconButton(onClick = { event(SearchFriendEvent.Close) }) {
                            Icon(painterResource(R.drawable.ic_close_line), contentDescription = null)
                        }
                    }
                )
            },
        ) {
            Column {
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                        )
                    },
                    contentColor = MaterialTheme.colorScheme.primary,
                    backgroundColor = Color.Transparent

                ) {
                    tabTitles.forEachIndexed { index, title ->
                        val selected = pagerState.currentPage == index
                        Tab(selected = selected,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.scrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = title.uppercase(),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (selected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6F)
                                    }
                                )
                            })
                    }
                }
                HorizontalPager(
                    count = tabTitles.size,
                    state = pagerState,
                ) { tabIndex ->
                    when (tabIndex) {
                        0 -> SearchIdContent(
                            myProfile = myProfile,
                            onSearchId = {
                                event(SearchFriendEvent.SearchById(it))
                            },
                            onOpenCreateAddeepId = {
                                event(SearchFriendEvent.OpenAddeepId)
                            },
                        )
                        1 -> SearchPhoneNumberContent(
                            countryCode,
                            onOpenCountryPicker = { event(SearchFriendEvent.OpenCountryList) },
                            onSearchPhoneNumber = { event(SearchFriendEvent.SearchByPhone(it)) }
                        )
                    }
                }
            }
            searchedFriend.takeIf { it is ViewState.Success<*> && it.data != null }?.let {
                val friend = (it as ViewState.Success<User>).data
                SearchedFriendContent(event, friend!!)
            }
        }
    }
}

@Composable
fun SearchIdContent(
    myProfile: User?,
    onSearchId: (String) -> Unit,
    onOpenCreateAddeepId: () -> Unit
) {
    var addeepId by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            searchText = addeepId,
            hint = stringResource(R.string.search_friend_addeep_id_hint),
            onSearch = { addeepId = it },
            onSearchButton = {
                onSearchId(addeepId)
                focusManager.clearFocus(true)
            }
        )
        TextButton(onClick = { onOpenCreateAddeepId() }) {
            val text = myProfile?.addeepId?.let { stringResource(R.string.search_friend_as_addeep_id, it) }
                ?: stringResource(R.string.search_friend_create_addeep_id)
            Text(
                text,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun SearchPhoneNumberContent(
    countryCode: String?,
    onOpenCountryPicker: () -> Unit,
    onSearchPhoneNumber: (String) -> Unit,
) {
    val country by remember { mutableStateOf(Countries.first { it.nameCode == (countryCode ?: "kr") }) }
    var phone by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var showErrorPhoneNumber by remember { mutableStateOf(false) }


    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .width(80.dp)
                    .clickable {
                        focusManager.clearFocus(true)
                        onOpenCountryPicker()
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "+${country.phoneCode}",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    )
                    Icon(
                        painterResource(R.drawable.ic_dropdown),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                    )
                }
                Divider()
            }

            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                searchText = phone,
                hint = stringResource(R.string.search_friend_phone_hint),
                onSearch = { phone = it },
                onSearchButton = {
                    formatNumberToE164(phone.trim(), country.nameCode.uppercase()).let {
                        if (it == null) {
                            showErrorPhoneNumber = true
                        } else {
                            onSearchPhoneNumber(it)

                        }
                    }
                    focusManager.clearFocus(true)
                }
            )
        }
    }

    if (showErrorPhoneNumber) {
        AlertDialog(
            icon = {
                Icon(
                    painterResource(R.drawable.ic_error_fill),
                    contentDescription = null,
                )
            },
            title = { Text(stringResource(R.string.login_invalid_phone_title)) },
            text = { Text(stringResource(R.string.login_invalid_phone_description)) },
            dismissButton = {
                TextButton(onClick = {
                    showErrorPhoneNumber = false
                }) {
                    Text(stringResource(R.string.common_ok))
                }
            },
            confirmButton = {},
            onDismissRequest = {},
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }
}

@Composable
fun SearchedFriendContent(
    event: (SearchFriendEvent) -> Unit,
    friend: User
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 240.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd
        ) {
            AsyncImage(
                ImageRequest.Builder(context = LocalContext.current)
                    .data(friend.getAvatarUrl())
                    .placeholder(R.drawable.placeholder_avatar)
                    .error(R.drawable.placeholder_avatar)
                    .fallback(R.drawable.placeholder_avatar)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 20.dp)
                    .size(96.dp)
                    .clip(RoundedCornerShape(32.dp)),
            )
            IconButton(
                onClick = { event(SearchFriendEvent.AddFriend(friend.id)) },
                modifier = Modifier
                    .size(40.dp)
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.tertiary, CircleShape),
            ) {
                Icon(painterResource(R.drawable.ic_add_person_line), contentDescription = null, tint = Color.White)
            }
        }
        Text(friend.name, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
    }
}