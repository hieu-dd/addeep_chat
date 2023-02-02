package net.itanchi.addeep.android.ui.screen.spash.tos

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.itanchi.addeep.android.util.ViewState

@Preview
@Composable
fun TermsOfServiceScreenPreview() {
    TermsOfServiceScreenContent(
        event = {},
        termsOfServiceViewState = ViewState.Idle,
    )
}