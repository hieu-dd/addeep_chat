package net.itanchi.addeep.android.ui.screen.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun LoaddingDialog(
    title: String,
    isLoading: Boolean = false,
) {
    if (isLoading) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp)
            ) {
                Text(title)
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
    }
}

@Preview
@Composable
fun LoaddingDialogPreview() {
    LoaddingDialog(
        title = "Loading...",
        isLoading = true,
    )
}

@Composable
fun WarningDialog(
    title: String?,
    content: String,
    isWarning: Boolean = false,
    dismissText: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (isWarning) {
        AlertDialog(
            title = title?.let {
                { Text(text = title) }
            },
            text = { Text(text = content) },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(dismissText)
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(confirmText)
                }
            },
            onDismissRequest = {},
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }
}

@Preview
@Composable
fun WarningDialogPreview() {
    WarningDialog(
        title = "Loading...",
        content = "Warning",
        isWarning = true,
        dismissText = "Dismiss",
        confirmText = "Confirm",
        onDismiss = {},
        onConfirm = {},
    )
}

@Composable
fun ErrorDialog(
    title: String,
    content: String,
    isError: Boolean = false,
    dismissText: String,
    onDismiss: () -> Unit,
) {
    if (isError) {
        AlertDialog(
            title = { Text(text = title) },
            text = { Text(text = content) },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(dismissText)
                }
            },
            confirmButton = {},
            onDismissRequest = {},
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }
}

@Preview
@Composable
fun ErrorDialogPreview() {
    ErrorDialog(
        title = "Loading...",
        content = "Error",
        isError = true,
        dismissText = "Dismiss",
        onDismiss = {},
    )
}

@Composable
fun InfoDialog(
    content: String,
    isShown: Boolean = false,
    dismissText: String,
    onDismiss: () -> Unit,
) {
    if (isShown) {
        AlertDialog(
            text = { Text(text = content) },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(dismissText)
                }
            },
            confirmButton = {},
            onDismissRequest = onDismiss,
        )
    }
}

@Preview
@Composable
fun InfoDialogPreview() {
    InfoDialog(
        content = "Some info",
        isShown = true,
        dismissText = "OK",
        onDismiss = {},
    )
}

@Composable
fun FunctionalityNotAvailableDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = "Functionality not available \uD83D\uDE48",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "CLOSE")
            }
        }
    )
}

@Preview
@Composable
fun FunctionalityNotAvailableDialogPreview() {
    FunctionalityNotAvailableDialog(
        onDismiss = {},
    )
}

@Composable
fun ConfirmDialog(
    title: String,
    content: String,
    isShown: Boolean = false,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (isShown) {
        AlertDialog(
            title = { Text(text = title) },
            text = { Text(text = content) },
            dismissButton = {},
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(confirmText)
                }
            },
            onDismissRequest = onDismiss,
        )
    }
}