package net.itanchi.addeep.android.ui.screen.auth.otp

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import net.itanchi.addeep.android.R
import net.itanchi.addeep.android.ui.screen.auth.otp.components.OTPRow
import net.itanchi.addeep.android.ui.screen.common.LoaddingDialog
import net.itanchi.addeep.android.util.FlowAsState
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.android.util.findActivity
import org.koin.androidx.compose.getViewModel
import java.util.concurrent.TimeUnit

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun OTPScreen(
    phone: String,
    viewModel: OTPViewModel = getViewModel(),
) {
    val otpViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.otpViewState,
        viewModel.otpViewState.value,
    )
    OTPScreenContent(
        viewModel::handleEvent,
        otpViewState,
        phone,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPScreenContent(
    event: (OTPEvent) -> Unit,
    otpViewState: ViewState,
    phone: String,
) {
    var otp by rememberSaveable { mutableStateOf(CharArray(6)) }
    var otpVerificationId by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<Throwable?>(null) }
    var loading by rememberSaveable { mutableStateOf(false) }

    val totalTime = remember { 90 * 1000 }
    var currentTime by rememberSaveable { mutableStateOf(totalTime) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    when (otpViewState) {
        is ViewState.Error -> {
            loading = false
            error = otpViewState.cause
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

    LaunchedEffect(currentTime) {
        if (currentTime == totalTime) {
            sendCode(
                context = context,
                phone = phone,
                timeout = totalTime.toLong() / 1000,
                onCodeSent = { otpVerificationId = it },
                onVerificationCompleted = { credential ->
                    val newOTP = otp.clone()
                    credential.smsCode?.toCharArray()?.forEachIndexed { index, char ->
                        newOTP[index] = char
                    }
                    otp = newOTP
                },
                onVerificationFailed = { error = it },
            )
        }
        if (currentTime > 0) {
            delay(1000)
            currentTime -= 1000
        }
    }

    LaunchedEffect(otp) {
        if (otp.all { it.isDigit() }) {
            loading = true
            verifyCode(
                verificationId = otpVerificationId,
                code = String(otp),
                onSuccess = { token ->
                    event(OTPEvent.Login(token))
                },
                onError = {
                    loading = false
                    error = it
                },
            )
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    stringResource(R.string.otp_title),
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 24.dp),
                    style = MaterialTheme.typography.titleLarge,
                )

                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))) {
                            append(stringResource(R.string.otp_description))
                        }
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                            append(" $phone")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )

                Spacer(modifier = Modifier.height(16.dp))

                OTPRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    focusManager = focusManager,
                    otp = otp,
                    onOTPChange = { position, char ->
                        otp = CharArray(6) {
                            if (it == position) char else otp[it]
                        }
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                if (currentTime > 0) {
                    Text(
                        stringResource(R.string.otp_resend_code_waiting, currentTime / 1000),
                        modifier = Modifier.padding(start = 16.dp, bottom = 24.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                } else {
                    Text(
                        stringResource(R.string.otp_resend_code),
                        modifier = Modifier.padding(start = 16.dp, bottom = 24.dp)
                            .clickable { currentTime = totalTime },
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }

    LoaddingDialog(
        title = stringResource(R.string.otp_verifying),
        isLoading = loading,
    )

    ErrorDialog(
        error = error,
        onDismiss = { event(OTPEvent.DismissError) }
    )
}

@Composable
private fun ErrorDialog(
    error: Throwable?,
    onDismiss: () -> Unit,
) {
    if (error != null) {
        var title = stringResource(R.string.common_error_title)
        var description = stringResource(R.string.common_error_description)

        when (error) {
            is FirebaseAuthException -> {
                title = stringResource(R.string.otp_invalid_otp_title)
                description = stringResource(R.string.otp_invalid_otp_description)
            }
        }
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

private fun sendCode(
    context: Context,
    phone: String,
    timeout: Long,
    onCodeSent: (String) -> Unit,
    onVerificationCompleted: (PhoneAuthCredential) -> Unit,
    onVerificationFailed: (Throwable) -> Unit,
) {
    context.findActivity()?.let {
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phone)
            .setTimeout(timeout, TimeUnit.SECONDS)
            .setActivity(it)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(
                    credential: PhoneAuthCredential,
                ) {
                    onVerificationCompleted(credential)
                }

                override fun onVerificationFailed(
                    exception: FirebaseException,
                ) {
                    onVerificationFailed(exception)
                }

                override fun onCodeSent(
                    verificationId: String,
                    forceResendingToken: PhoneAuthProvider.ForceResendingToken,
                ) {
                    onCodeSent(verificationId)
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}

private suspend fun verifyCode(
    verificationId: String,
    code: String,
    onSuccess: (String) -> Unit,
    onError: (Throwable) -> Unit,
) {
    try {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        Firebase.auth.signInWithCredential(credential).await()?.user?.let {
            it.getIdToken(false).await().token?.let { token ->
                onSuccess(token)
            } ?: onError(Throwable())
        } ?: onError(Throwable())
    } catch (error: Throwable) {
        onError(error)
    }
}