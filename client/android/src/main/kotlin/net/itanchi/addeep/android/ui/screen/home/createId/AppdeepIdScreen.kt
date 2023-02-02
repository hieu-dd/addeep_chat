package net.itanchi.addeep.android.ui.screen.home.createId

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Switch
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.itanchi.addeep.android.R
import net.itanchi.addeep.android.data.Consts.MAX_LENGTH_ADDEEP_ID
import net.itanchi.addeep.android.data.Consts.MIN_LENGTH_ADDEEP_ID
import net.itanchi.addeep.android.ui.screen.common.ConfirmDialog
import net.itanchi.addeep.android.ui.screen.common.InfoDialog
import net.itanchi.addeep.android.util.FlowAsState
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.core.data.model.User
import org.koin.androidx.compose.getViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AddeepIdScreen(viewModel: AddeepIdViewModel = getViewModel()) {
    val myProfileViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.myProfileViewState,
        viewModel.myProfileViewState.value
    )
    AddeepIdContent(event = viewModel::handleEvent, myProfileViewState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddeepIdContent(
    event: (AddeepIdEvent) -> Unit,
    myProfileViewState: ViewState,
) {
    if (myProfileViewState is ViewState.Success<*>) {
        val myProfile = (myProfileViewState as ViewState.Success<User>).data
        val myAddeepId = myProfile?.addeepId
        val isCreateAddeepId = myAddeepId.isNullOrBlank()
        Surface {
            Scaffold(
                topBar = {
                    SmallTopAppBar(
                        title = {
                            val title =
                                stringResource(R.string.create_addeep_id_register_id).takeIf { isCreateAddeepId }
                                    ?: stringResource(R.string.create_addeep_id_addeep_id)
                            Text(title)
                        },
                        navigationIcon = {
                            IconButton(onClick = { event(AddeepIdEvent.Back) }) {
                                Icon(painterResource(R.drawable.ic_chevron_left), contentDescription = null)
                            }
                        }
                    )
                },
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (isCreateAddeepId) {
                        CreateAddeepIdContent(onCreateAddeepId = { event(AddeepIdEvent.RegisterAddeepId(it)) })
                    } else {
                        ToggleSearchableAddeepIdContent(
                            addeepId = myAddeepId!!,
                            allowToSearchByAddeepId = myProfile.allowToSearchByAddeepId,
                            onToggleSearchableAddeepId = {
                                event(AddeepIdEvent.ToggleSearchableAddeepId(it))
                            })
                    }
                }
            }
        }
    }
}

@Composable
fun CreateAddeepIdContent(onCreateAddeepId: (String) -> Unit) {
    val focusManager = LocalFocusManager.current
    var addeepId by remember { mutableStateOf("") }
    var showConfirmCreateIdDialog by remember { mutableStateOf(false) }

    Column {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = addeepId,
            textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
            onValueChange = { addeepId = it },
            singleLine = true,
            label = {
                Text(stringResource(R.string.create_addeep_id_addeep_id))
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = {
                showConfirmCreateIdDialog = true
                focusManager.clearFocus(true)
            })
        )

        Text(
            "${addeepId.trim().length} / ${MAX_LENGTH_ADDEEP_ID}",
            modifier = Modifier
                .padding(end = 16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(0.6F)
            )
        )
        Text(
            stringResource(R.string.create_addeep_id_tutorial, MIN_LENGTH_ADDEEP_ID, MAX_LENGTH_ADDEEP_ID),
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6F)
            )
        )
        Button(
            modifier = Modifier
                .padding(top = 36.dp)
                .fillMaxWidth()
                .height(40.dp),
            onClick = {
                showConfirmCreateIdDialog = true
            },
            enabled = addeepId.trim().length in MIN_LENGTH_ADDEEP_ID..MAX_LENGTH_ADDEEP_ID,
            shape = RoundedCornerShape(20.dp),
        ) {
            Text(stringResource(R.string.common_confirm), color = MaterialTheme.colorScheme.onPrimary)
        }
    }
    ConfirmDialog(
        title = stringResource(id = R.string.create_addeep_id_new_addeep_id),
        content = stringResource(R.string.create_addeep_id_confirm_description, addeepId.trim()),
        isShown = showConfirmCreateIdDialog,
        confirmText = stringResource(R.string.common_ok),
        onConfirm = {
            onCreateAddeepId(addeepId.trim())
            showConfirmCreateIdDialog = false
        },
        onDismiss = {
            showConfirmCreateIdDialog = false
        }
    )
}

@Composable
fun ToggleSearchableAddeepIdContent(
    addeepId: String,
    allowToSearchByAddeepId: Boolean?,
    onToggleSearchableAddeepId: (Boolean) -> Unit,
) {
    var searchable by remember { mutableStateOf(false) }
    var showWarningDialog by remember { mutableStateOf(false) }
    var focusManager = LocalFocusManager.current
    Column {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    showWarningDialog = it.isFocused
                },
            value = addeepId,
            textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
            readOnly = true,
            onValueChange = { },
            singleLine = true,
            label = {
                Text(stringResource(R.string.common_id).uppercase())
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colorScheme.background
            )
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column {
                Text(
                    stringResource(R.string.create_addeep_id_make_searchable_id),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    stringResource(R.string.create_addeep_id_allow_searchable_id),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6F)
                    )
                )
            }
            Switch(
                checked = allowToSearchByAddeepId ?: searchable,
                onCheckedChange = {
                    searchable = it
                    onToggleSearchableAddeepId(it)
                }
            )
        }
        Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)
    }
    InfoDialog(
        content = stringResource(R.string.create_addeep_id_register_id_can_not_change),
        isShown = showWarningDialog,
        dismissText = stringResource(R.string.common_ok),
        onDismiss = {
            showWarningDialog = false
            focusManager.clearFocus(true)
        }
    )
}



