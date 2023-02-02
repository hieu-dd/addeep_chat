package net.itanchi.addeep.android.ui.screen.home.friends.invite

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import net.itanchi.addeep.android.R
import net.itanchi.addeep.android.ui.screen.common.InfoDialog
import net.itanchi.addeep.android.util.FlowAsState
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.android.util.getOrNull
import net.itanchi.addeep.android.util.shareAnotherApp
import org.koin.androidx.compose.getViewModel


@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteFriendScreen(
    viewModel: InviteFriendsViewModel = getViewModel()
) {
    val myProfileViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.myProfileViewState,
        viewModel.myProfileViewState.value,
    )
    InviteFriendContent(viewModel::handleEvent, myProfileViewState)
}

@SuppressLint("ServiceCast")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteFriendContent(
    event: (InviteFriendsEvent) -> Unit,
    myProfileViewState: ViewState,
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val shareAddeepId =
        myProfileViewState.getOrNull()?.let { stringResource(R.string.invite_friend_share_content_addeep_id, it) }
            .orEmpty()
    val shareContent = stringResource(R.string.invite_friend_share_content, shareAddeepId).trim()
    val showCopyClipboardDialog = remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
            SmallTopAppBar(
                modifier = Modifier.background(MaterialTheme.colorScheme.onPrimary),
                title = { Text(stringResource(R.string.invite_friends_title)) },
                navigationIcon = {
                    IconButton(onClick = { event(InviteFriendsEvent.Back) }) {
                        Icon(painterResource(R.drawable.ic_close_line), contentDescription = null)
                    }
                })
        }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    stringResource(R.string.invite_friends_via).uppercase(),
                    modifier = Modifier.padding(top = 12.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6F),
                )
                InviteFriendRow(title = stringResource(R.string.invite_friends_sms), hasTrailingIcon = true) {
                    event(InviteFriendsEvent.InviteViaContact(InviteType.SMS))
                }
                InviteFriendRow(title = stringResource(R.string.invite_friends_email), hasTrailingIcon = true) {
                    event(InviteFriendsEvent.InviteViaContact(InviteType.EMAIL))
                }
                InviteFriendRow(title = stringResource(R.string.invite_friends_via_apps)) {
                    context.shareAnotherApp(shareContent)
                }
                InviteFriendRow(title = stringResource(R.string.invite_friends_via_copy_link)) {
                    showCopyClipboardDialog.value = true
                    clipboardManager.setText(AnnotatedString(shareContent))
                }
            }
        }
    }

    InfoDialog(
        content = stringResource(R.string.invite_friends_link_is_copied),
        isShown = showCopyClipboardDialog.value,
        dismissText = stringResource(R.string.common_ok),
        onDismiss = { showCopyClipboardDialog.value = false }
    )
}

@Composable
fun InviteFriendRow(
    modifier: Modifier = Modifier,
    title: String,
    hasTrailingIcon: Boolean = false,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.clickable { onClick() }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (hasTrailingIcon) {
                Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = null)
            }
        }
        Divider(modifier = Modifier.padding(horizontal = 16.dp))
    }
}