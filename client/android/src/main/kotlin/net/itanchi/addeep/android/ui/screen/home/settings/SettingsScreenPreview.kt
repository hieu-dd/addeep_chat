package net.itanchi.addeep.android.ui.screen.home.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.itanchi.addeep.android.ui.theme.AppTheme

@Preview
@Composable
fun SettingRowDarkThemePreview() {
    AppTheme(isDarkTheme = true) {
        SettingRow(
            icon = Icons.Rounded.Settings,
            label = "Settings",
            isOpenNewScreen = true,
            onClick = {},
        )
    }
}

@Preview
@Composable
fun SettingRowLightkThemePreview() {
    AppTheme(isDarkTheme = false) {
        SettingRow(
            icon = Icons.Rounded.Settings,
            label = "Settings",
            isOpenNewScreen = true,
            onClick = {},
        )
    }
}
