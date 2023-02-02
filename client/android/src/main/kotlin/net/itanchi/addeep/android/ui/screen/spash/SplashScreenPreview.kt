package net.itanchi.addeep.android.ui.screen.spash

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.core.data.model.User

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreenContent(
        event = {},
        splashViewState = ViewState.Success<User>(null)
    )
}