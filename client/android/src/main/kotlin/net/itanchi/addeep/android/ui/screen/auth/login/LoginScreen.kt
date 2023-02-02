package net.itanchi.addeep.android.ui.screen.auth.login

import android.annotation.SuppressLint
import android.telephony.PhoneNumberUtils.formatNumberToE164
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.SavedStateHandle
import net.itanchi.addeep.android.R
import net.itanchi.addeep.android.data.Countries
import net.itanchi.addeep.android.util.FlowAsState
import net.itanchi.addeep.android.util.PrefixTransformation
import net.itanchi.addeep.android.util.ViewState
import org.koin.androidx.compose.getViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun LoginScreen(
    savedStateHandle: SavedStateHandle,
    viewModel: LoginViewModel = getViewModel(),
) {
    val countryCode = savedStateHandle.get<String>("countryCode")
    val loginViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.loginViewState,
        viewModel.loginViewState.value,
    )
    LoginScreenContent(
        viewModel::handleEvent,
        loginViewState,
        countryCode,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenContent(
    event: (LoginEvent) -> Unit,
    loginViewState: ViewState,
    countryCode: String?,
) {
    val countryInteractionSource = remember { MutableInteractionSource() }
    var country by remember { mutableStateOf(Countries.first { it.nameCode == (countryCode ?: "kr") }) }
    var phone by rememberSaveable { mutableStateOf("") }
    var showErrorPhoneNumber by remember { mutableStateOf(false) }
    var showConfirmPhoneNumber by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    if (countryInteractionSource.collectIsPressedAsState().value) {
        focusManager.clearFocus(true)
        event(LoginEvent.OpenCountryList)
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                LoginButton {
                    if (formatNumberToE164(phone.trim(), country.nameCode.uppercase()) == null) {
                        showErrorPhoneNumber = true
                    } else {
                        showConfirmPhoneNumber = true
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    stringResource(R.string.login_title),
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 24.dp),
                    style = MaterialTheme.typography.titleLarge,
                )

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    TextField(
                        modifier = Modifier.width(130.dp).wrapContentHeight(),
                        label = {
                            Text(
                                stringResource(R.string.login_country_input_label),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            )
                        },
                        value = country.name,
                        onValueChange = { },
                        singleLine = true,
                        readOnly = true,
                        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                        trailingIcon = {
                            Icon(painterResource(R.drawable.ic_dropdown), contentDescription = null)
                        },
                        interactionSource = countryInteractionSource,
                    )

                    TextField(
                        modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
                        label = {
                            Text(
                                stringResource(R.string.login_phone_input_label),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            )
                        },
                        value = phone,
                        onValueChange = { phone = it },
                        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                        singleLine = true,
                        visualTransformation = PrefixTransformation("+${country.phoneCode} "),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(true) }),
                    )
                }
            }

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

    if (showConfirmPhoneNumber) {
        val formattedPhoneNumber = formatNumberToE164(phone.trim(), country.nameCode.uppercase())

        AlertDialog(
            title = { Text(formattedPhoneNumber) },
            text = { Text(stringResource(R.string.login_confirm_phone_description)) },
            dismissButton = {
                TextButton(onClick = {
                    showConfirmPhoneNumber = false
                }) {
                    Text(stringResource(R.string.common_cancel))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmPhoneNumber = false
                    event(LoginEvent.SendOTP(formattedPhoneNumber))
                }) {
                    Text(stringResource(R.string.common_confirm))
                }
            },
            onDismissRequest = {},
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }
}

@Composable
private fun LoginButton(
    onClick: () -> Unit,
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
    ) {
        Icon(
            painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onTertiaryContainer,
        )
    }
}