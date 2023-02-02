package net.itanchi.addeep.android.ui.screen.permission

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import net.itanchi.addeep.android.ui.screen.common.WarningDialog
import org.koin.androidx.compose.getViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun PermissionScreen(
    viewModel: PermissionViewModel = getViewModel(),
) {
    PermissionScreenContent(viewModel::handleEvent)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreenContent(
    event: (PermissionEvent) -> Unit,
) {
    val openDialog = remember { mutableStateOf(true) }
    val contactsPermissionState = rememberPermissionState(android.Manifest.permission.READ_CONTACTS)

    PermissionRequired(
        permissionState = contactsPermissionState,
        permissionNotGrantedContent = {
            Rationale(
                onRequestPermission = { contactsPermissionState.launchPermissionRequest() }
            )
        },
        permissionNotAvailableContent = {
            Rationale(
                onRequestPermission = { openDialog.value = true }
            )
            if (openDialog.value) {
                WarningDialog(
                    title = null,
                    content = """
                        You're unable to use this feature without the required permissions.
                        Tap the Settings button to allow addeep to access the followings:
                        - Required:Contacts
                    """.trimIndent(),
                    isWarning = openDialog.value,
                    dismissText = "Cancel",
                    confirmText = "Settings",
                    onDismiss = {
                        openDialog.value = false
                    },
                    onConfirm = {
                        openDialog.value = false
                        event(PermissionEvent.NavigateToPhoneSettings)
                    }
                )
            }
        }
    ) {
        event(PermissionEvent.NavigateToHome)
    }
}

@Composable
fun Rationale(
    onRequestPermission: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text("Permissions", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            Text("addeep needs access to", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(32.dp))

            PermissionInfo(
                title = "Contacts",
                description = """
                - Allow you to send Contacts throw addeep
                - Allow you to add friends on addeep
            """.trimIndent()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.Info,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    "Tap the Accept button below to get started",
                    modifier = Modifier.padding(start = 4.dp).weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onRequestPermission) {
                Text("Accept")
            }
        }
    }
}

@Preview
@Composable
fun RationalePreview() {
    Rationale {}
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PermissionInfo(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    descriptionMaxLines: Int = 4,
) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )
    Column(
        modifier = modifier
            .clickable { expandedState = !expandedState }
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier
                    .alpha(ContentAlpha.medium)
                    .rotate(rotationState),
            )
        }
        if (expandedState) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = descriptionMaxLines,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider()
    }
}