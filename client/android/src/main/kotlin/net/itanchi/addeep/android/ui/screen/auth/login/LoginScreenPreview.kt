package net.itanchi.addeep.android.ui.screen.auth.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.itanchi.addeep.android.util.ViewState

@Preview
@Composable
fun LoginScreenContentnPreview() {
    LoginScreenContent(
        event = {},
        loginViewState = ViewState.Idle,
        countryCode = "",
    )
}