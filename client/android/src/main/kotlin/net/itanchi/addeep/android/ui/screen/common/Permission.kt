package net.itanchi.addeep.android.ui.screen.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import net.itanchi.addeep.android.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Permission(
    modifier: Modifier,
    permissions: Map<String, String>,
    onPermissionAccept: () -> Unit,
    openPhoneSettings: () -> Unit,
) {
    val openDialog = remember { mutableStateOf(true) }
    val permissionState = rememberPermissionState(permissions.keys.first())

    PermissionRequired(
        permissionState = permissionState,
        permissionNotGrantedContent = {
            Rationale(
                modifier = modifier,
                permissions = permissions,
                onRequestPermission = { permissionState.launchPermissionRequest() }
            )
        },
        permissionNotAvailableContent = {
            Rationale(
                modifier = modifier,
                permissions = permissions,
                onRequestPermission = { openDialog.value = true }
            )
            if (openDialog.value) {
                WarningDialog(
                    title = null,
                    content = stringResource(R.string.permission_required_description),
                    isWarning = openDialog.value,
                    dismissText = stringResource(R.string.common_cancel),
                    confirmText = stringResource(R.string.permission_go_to_settings),
                    onDismiss = {
                        openDialog.value = false
                    },
                    onConfirm = {
                        openDialog.value = false
                        openPhoneSettings()
                    }
                )
            }
        }
    ) {
        onPermissionAccept()
    }
}

@Composable
fun Rationale(
    modifier: Modifier,
    permissions: Map<String, String>,
    onRequestPermission: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                stringResource(R.string.permission),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                permissions.values.first(),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRequestPermission,
                modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp),
            ) {
                Text(text = stringResource(R.string.permission_accept))
            }
        }
    }
}

@Preview
@Composable
fun RationalePreview() {
    Rationale(
        modifier = Modifier,
        permissions = mapOf(
            android.Manifest.permission.READ_CONTACTS to stringResource(R.string.permission_contacts_description),
        )
    ) {}
}