package net.itanchi.addeep.android.ui.screen.home.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import net.itanchi.addeep.android.BuildConfig
import net.itanchi.addeep.android.ui.screen.common.ErrorDialog
import net.itanchi.addeep.android.ui.screen.common.LoaddingDialog
import net.itanchi.addeep.android.util.FlowAsState
import net.itanchi.addeep.android.util.ViewState
import org.koin.androidx.compose.getViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = getViewModel(),
) {
    val settingViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.settingViewState,
        viewModel.settingViewState.value
    )
    SettingsScreenContent(
        event = viewModel::handleEvent,
        settingViewState = settingViewState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    event: (SettingsEvent) -> Unit,
    settingViewState: ViewState,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                SmallTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { event(SettingsEvent.GoBack) }) {
                            Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                        }
                    },
                    title = { Text(text = "Settings") }
                )
            }
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Account",
                    modifier = Modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingRow(
                    icon = Icons.Rounded.Savings,
                    label = "Income Detail",
                    isOpenNewScreen = true,
                    onClick = { }
                )

                SettingRow(
                    icon = Icons.Rounded.Logout,
                    label = "Logout",
                    isOpenNewScreen = false,
                    onClick = { event(SettingsEvent.Logout) }
                )

                SettingRow(
                    icon = Icons.Rounded.Delete,
                    label = "Delete Account",
                    isOpenNewScreen = false,
                    onClick = { }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Customer Care",
                    modifier = Modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingRow(
                    icon = Icons.Rounded.Description,
                    label = "Notice Board",
                    isOpenNewScreen = true,
                    onClick = { }
                )

                SettingRow(
                    icon = Icons.Rounded.Quiz,
                    label = "FAQ",
                    isOpenNewScreen = true,
                    onClick = { }
                )

                Row(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(24.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Ver. ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }

    Dialogs(
        settingViewState,
        onDismissErrorDialog = { event(SettingsEvent.DismissError) }
    )
}

@Composable
fun Dialogs(
    settingViewState: ViewState,
    onDismissErrorDialog: () -> Unit,
) {
    var showLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    when (settingViewState) {
        is ViewState.Loading -> {
            showLoading = true
            showError = false
        }
        is ViewState.Error -> {
            showLoading = false
            showError = true
        }
        else -> {
            showLoading = false
            showError = false
        }
    }

    LoaddingDialog(
        title = "Loading...",
        isLoading = showLoading,
    )

    ErrorDialog(
        title = "Oops",
        content = """
            An unexpected error has occurred.
            Please try again.
        """.trimIndent(),
        isError = showError,
        dismissText = "OK",
        onDismiss = onDismissErrorDialog
    )
}

@Composable
fun SettingRow(
    icon: ImageVector,
    label: String,
    isOpenNewScreen: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.padding(16.dp),
            tint = MaterialTheme.colorScheme.onSurface,
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(top = 16.dp),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                if (isOpenNewScreen) {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.padding(top = 16.dp, end = 16.dp),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Divider(modifier = Modifier.padding(top = 16.dp))
        }
    }
}
