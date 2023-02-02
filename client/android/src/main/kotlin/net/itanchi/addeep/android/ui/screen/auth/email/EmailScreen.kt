package net.itanchi.addeep.android.ui.screen.auth.email

import android.annotation.SuppressLint
import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import net.itanchi.addeep.android.R
import net.itanchi.addeep.android.ui.screen.auth.email.components.TermRow
import net.itanchi.addeep.android.ui.screen.common.LoaddingDialog
import net.itanchi.addeep.android.util.FlowAsState
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.core.data.model.Preferences
import org.koin.androidx.compose.getViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun EmailScreen(
    viewModel: EmailViewModel = getViewModel(),
) {
    val emailViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.emailViewState,
        viewModel.emailViewState.value,
    )
    EmailScreenContent(
        viewModel::handleEvent,
        emailViewState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailScreenContent(
    event: (EmailEvent) -> Unit,
    emailViewState: ViewState,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var term by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<Throwable?>(null) }
    var loading by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    when (emailViewState) {
        is ViewState.Error -> {
            loading = false
            error = emailViewState.cause
        }
        is ViewState.Loading -> {
            loading = true
            error = null
        }
        is ViewState.Idle -> {
            loading = false
            error = null
        }
        else -> {}
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    stringResource(R.string.email_title),
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 24.dp),
                    style = MaterialTheme.typography.titleLarge,
                )

                TextField(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    label = { Text(stringResource(R.string.email_email)) },
                    value = email,
                    onValueChange = { email = it },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
                    singleLine = true,
                    leadingIcon = {
                        Icon(painterResource(R.drawable.ic_email_line), contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(true) }),
                )

                Spacer(modifier = Modifier.height(36.dp))

                TermRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    termTitle = stringResource(R.string.email_term_title),
                    termDescription = stringResource(R.string.email_term_description),
                    checked = term,
                    onChecked = { term = it },
                    onClicked = {},
                )

                Button(
                    onClick = { event(EmailEvent.AddEmail(email, Preferences(collectAndUsePersonalInfo = term))) },
                    modifier = Modifier.fillMaxWidth().padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 16.dp),
                    enabled = email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                ) {
                    Text(text = stringResource(R.string.common_confirm))
                }

                TextButton(
                    onClick = { event(EmailEvent.Skip) },
                    modifier = Modifier.wrapContentSize(),
                ) {
                    Text(text = stringResource(R.string.common_skip))
                }
            }

        }
    }

    LoaddingDialog(
        title = stringResource(R.string.common_loading),
        isLoading = loading,
    )

    ErrorDialog(
        error = error,
        onDismiss = { event(EmailEvent.DismissError) }
    )

}

@Composable
private fun ErrorDialog(
    error: Throwable?,
    onDismiss: () -> Unit,
) {
    if (error != null) {
        val title = stringResource(R.string.common_error_title)
        val description = stringResource(R.string.common_error_description)

        AlertDialog(
            icon = { Icon(painterResource(R.drawable.ic_error_fill), contentDescription = null) },
            title = { Text(title) },
            text = { Text(description) },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.common_ok))
                }
            },
            confirmButton = {},
            onDismissRequest = {},
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }
}