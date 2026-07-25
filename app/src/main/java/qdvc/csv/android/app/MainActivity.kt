package qdvc.csv.android.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: CsvViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIncomingIntent(intent)

        setContent {
            App(viewModel = viewModel)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return
        val uri: Uri? = when (intent.action) {
            Intent.ACTION_VIEW -> intent.data
            Intent.ACTION_SEND -> intent.getParcelableExtra(Intent.EXTRA_STREAM)
            else -> null
        }
        if (uri != null) {
            viewModel.load(uri)
        }
    }
}

private enum class Screen { VIEWER, SETTINGS }

@Composable
private fun App(viewModel: CsvViewModel) {
    val context = LocalContext.current
    val themePref = remember { ThemePreference(context.applicationContext) }
    val scope = rememberCoroutineScope()

    val themeMode by themePref.themeMode
        .collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
    val state by viewModel.state.collectAsStateWithLifecycle()

    var screen by remember { mutableStateOf(Screen.VIEWER) }

    QdvcCsvTheme(themeMode = themeMode) {
        Surface(modifier = Modifier.fillMaxSize()) {
            when (screen) {
                Screen.VIEWER -> ViewerScreen(
                    state = state,
                    onPickFile = { uri -> viewModel.load(uri) },
                    onOpenSettings = { screen = Screen.SETTINGS },
                )
                Screen.SETTINGS -> SettingsScreen(
                    current = themeMode,
                    onSelect = { mode ->
                        scope.launch { themePref.setThemeMode(mode) }
                    },
                    onBack = { screen = Screen.VIEWER },
                )
            }
        }
    }
}
