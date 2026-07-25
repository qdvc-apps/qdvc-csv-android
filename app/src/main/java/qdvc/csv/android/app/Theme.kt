package qdvc.csv.android.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2E5AAC),
    onPrimary = Color(0xFFFFFFFF),
    surface = Color(0xFFFDFDFD),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE7ECF3),
    onSurfaceVariant = Color(0xFF41474D),
    background = Color(0xFFFDFDFD),
    onBackground = Color(0xFF1A1C1E),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA9C4FF),
    onPrimary = Color(0xFF06305C),
    surface = Color(0xFF121417),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF2A2F36),
    onSurfaceVariant = Color(0xFFC1C7CE),
    background = Color(0xFF121417),
    onBackground = Color(0xFFE2E2E6),
)

@Composable
fun QdvcCsvTheme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit,
) {
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        content = content,
    )
}
