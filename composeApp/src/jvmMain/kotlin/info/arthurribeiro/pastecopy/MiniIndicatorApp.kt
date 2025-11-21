package info.arthurribeiro.pastecopy

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import info.arthurribeiro.pastecopy.ui.components.FloatingIndicator
import info.arthurribeiro.pastecopy.ui.viewmodel.ClipboardViewModel

@Composable
fun MiniIndicatorApp(
    viewModel: ClipboardViewModel,
    onOpenMain: () -> Unit
) {
    MaterialTheme {
        val items by viewModel.items.collectAsState()

        FloatingIndicator(
            itemCount = items.size,
            onClick = onOpenMain
        )
    }
}
