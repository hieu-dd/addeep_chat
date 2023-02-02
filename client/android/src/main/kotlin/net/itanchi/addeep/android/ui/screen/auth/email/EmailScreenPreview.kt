package net.itanchi.addeep.android.ui.screen.auth.email

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.itanchi.addeep.android.util.ViewState

@Preview
@Composable
fun EmailScreenContentnPreview() {
    EmailScreenContent(
        event = {},
        emailViewState = ViewState.Idle,
    )
}