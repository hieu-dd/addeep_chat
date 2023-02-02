package net.itanchi.addeep.android.ui.screen.auth.register

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import kotlinx.datetime.toKotlinLocalDate
import net.itanchi.addeep.android.R
import net.itanchi.addeep.android.ui.screen.auth.register.components.AvatarInput
import net.itanchi.addeep.android.ui.screen.auth.register.components.DoBInput
import net.itanchi.addeep.android.ui.screen.auth.register.components.GenderInput
import net.itanchi.addeep.android.ui.screen.auth.register.components.NameInput
import net.itanchi.addeep.android.ui.screen.common.LoaddingDialog
import net.itanchi.addeep.android.util.FlowAsState
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.core.data.model.Gender
import org.koin.androidx.compose.getViewModel
import java.time.LocalDate

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = getViewModel(),
) {
    val registerViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.registerViewState,
        viewModel.registerViewState.value,
    )
    RegisterScreenContent(
        viewModel::handleEvent,
        registerViewState,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun RegisterScreenContent(
    event: (RegisterEvent) -> Unit,
    registerViewState: ViewState,
) {
    val genders = remember { mutableStateListOf(Gender.Male.name, Gender.Female.name, Gender.Other.name) }
    var showAvatarInputDialog by rememberSaveable { mutableStateOf(false) }
    var avatar by rememberSaveable { mutableStateOf<Uri?>(null) }
    val maxNameLength = rememberSaveable { 20 }
    var name by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf<String?>(null) }
    var dob by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var error by rememberSaveable { mutableStateOf<Throwable?>(null) }
    var loading by rememberSaveable { mutableStateOf(false) }
    val isRegisterEnable = name.isNotBlank()

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    when (registerViewState) {
        is ViewState.Error -> {
            loading = false
            error = registerViewState.cause
        }
        is ViewState.Loading -> {
            loading = true
            error = null
        }
        is ViewState.Idle -> {
            loading = false
            error = null
        }
        else -> {
            loading = false
            error = null
        }
    }

    LaunchedEffect(avatar) {
        avatar?.let {
            context.contentResolver.openInputStream(it)?.let {
                event(RegisterEvent.UploadAvatar(it.readBytes()))
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { focusManager.clearFocus(true) }
                ),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier.size(96.dp),
                    contentAlignment = Alignment.BottomEnd,
                ) {
                    AsyncImage(
                        avatar ?: R.drawable.placeholder_avatar,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                            .clip(RoundedCornerShape(32.dp))
                            .clickable { showAvatarInputDialog = true },
                        contentScale = ContentScale.Crop,
                    )
                    Image(
                        painterResource(R.drawable.ic_photo_camera_line),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                            .clip(CircleShape)
                            .clickable { showAvatarInputDialog = true }
                            .background(MaterialTheme.colorScheme.secondary, CircleShape),
                        contentScale = ContentScale.Inside,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                NameInput(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    focusManager = focusManager,
                    label = stringResource(R.string.register_name),
                    name = name,
                    maxNameLength = maxNameLength,
                    onNameChange = { if (it.trim().length <= maxNameLength) name = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    DoBInput(
                        modifier = Modifier.weight(1f),
                        focusManager = focusManager,
                        label = stringResource(R.string.register_dob),
                        dob = dob,
                        onDoBChange = { dob = it }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    GenderInput(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.register_gender),
                        gender = gender,
                        genders = genders,
                        onGenderChange = { gender = it },
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        event(
                            RegisterEvent.Register(
                                name.trim(),
                                dob?.toKotlinLocalDate(),
                                gender?.let { Gender.valueOf(it) }
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                    enabled = isRegisterEnable
                ) {
                    Text(text = stringResource(R.string.common_ok))
                }
            }
        }
    }

    AvatarInput(
        isShown = showAvatarInputDialog,
        onAvatarInput = { uri ->
            uri?.let { avatar = it }
            showAvatarInputDialog = false
        },
    )

    LoaddingDialog(
        title = stringResource(R.string.common_continue),
        isLoading = loading,
    )

    ErrorDialog(
        error = error,
        onDismiss = { error = null }
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