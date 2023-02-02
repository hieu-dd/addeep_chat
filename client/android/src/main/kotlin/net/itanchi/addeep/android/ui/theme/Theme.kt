package net.itanchi.addeep.android.ui.theme

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val AppDarkColorScheme = darkColorScheme(
    primary = Color(0xFFB1C5FF),
    onPrimary = Color(0xFF002A79),
    primaryContainer = Color(0xFF003EA9),
    onPrimaryContainer = Color(0xFFDAE2FF),
//    inversePrimary = Blue40,
    secondary = Color(0xFFC1C6DD),
    onSecondary = Color(0xFF2A3042),
    secondaryContainer = Color(0xFF414659),
    onSecondaryContainer = Color(0xFFDCE1F9),
    tertiary = Color(0xFFE1BBDC),
    onTertiary = Color(0xFF412740),
    tertiaryContainer = Color(0xFF5A3D58),
    onTertiaryContainer = Color(0xFFFFD6F9),
    error = Color(0xFFFFB4A9),
    onError = Color(0xFF680003),
    errorContainer = Color(0xFF930006),
    onErrorContainer = Color(0xFFFFDAD4),
    background = Color(0xFF1B1B1F),
    onBackground = Color(0xFFE3E1E6),
    surface = Color(0xFF1B1B1F),
    onSurface = Color(0xFFE3E1E6),
//    inverseSurface = Grey90,
//    inverseOnSurface = Grey20,
    surfaceVariant = Color(0xFF44464E),
    onSurfaceVariant = Color(0xFFC6C6D0),
    outline = Color(0xFF8F909A)
)

private val AppLightColorScheme = lightColorScheme(
    primary = Color(0xFF346EFE),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBE6FF),
    onPrimaryContainer = Color(0xFF00154D),
//    inversePrimary = Blue80,
    secondary = Color(0xFF5F677C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD9E3FC),
    onSecondaryContainer = Color(0xFF273457),
    tertiary = Color(0xFF547364),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD6FFEB),
    onTertiaryContainer = Color(0xFF122B1F),
    error = Color(0xFFBA1B1B),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD4),
    onErrorContainer = Color(0xFF410001),
    background = Color(0xFFFAFBFF),
    onBackground = Color(0xFF1B1B1F),
    surface = Color(0xFFFAFBFF),
    onSurface = Color(0xFF1B1B1F),
//    inverseSurface = Grey20,
//    inverseOnSurface = Grey95,
    surfaceVariant = Color(0xFFE2E2EC),
    onSurfaceVariant = Color(0xFF44464E),
    outline = Color(0xFF75767F),
)

@SuppressLint("NewApi")
@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    isDynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val dynamicColor = isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when {
        dynamicColor && isDarkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !isDarkTheme -> dynamicLightColorScheme(LocalContext.current)
        isDarkTheme -> AppDarkColorScheme
        else -> AppLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography
    ) {
        // TODO (M3): MaterialTheme doesn't provide LocalIndication, remove when it does
        val rippleIndication = rememberRipple()
        CompositionLocalProvider(
            LocalIndication provides rippleIndication,
            content = content
        )
    }
}
