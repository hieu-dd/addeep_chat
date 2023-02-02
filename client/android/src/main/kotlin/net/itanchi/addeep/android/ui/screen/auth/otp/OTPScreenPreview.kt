package net.itanchi.addeep.android.ui.screen.auth.otp

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.itanchi.addeep.android.util.ViewState

@Preview
@Composable
fun OTPScreenContentnPreview() {
    OTPScreenContent(
        event = {},
        otpViewState = ViewState.Idle,
        phone = "+84389104xxx",
    )
}