package info.arthurribeiro.pastecopy

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import info.arthurribeiro.pastecopy.ui.screens.ClipboardHistoryScreen
import info.arthurribeiro.pastecopy.ui.screens.SettingsScreen
import info.arthurribeiro.pastecopy.ui.viewmodel.ClipboardViewModel

enum class Screen {
    HISTORY,
    SETTINGS
}

@Composable
fun JvmApp(viewModel: ClipboardViewModel) {
    var currentScreen by remember { mutableStateOf(Screen.HISTORY) }

    MaterialTheme {
        when (currentScreen) {
            Screen.HISTORY -> ClipboardHistoryScreen(
                viewModel = viewModel,
                onNavigateToSettings = { currentScreen = Screen.SETTINGS },
                modifier = Modifier
            )
            Screen.SETTINGS -> SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { currentScreen = Screen.HISTORY },
                modifier = Modifier
            )
        }
    }
}
