package info.arthurribeiro.pastecopy

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import info.arthurribeiro.pastecopy.platform.GlobalKeyboardListener
import java.awt.Dimension
import java.awt.Toolkit

fun main() = application {
    // Cria ViewModel compartilhado entre as janelas
    val viewModel = remember { info.arthurribeiro.pastecopy.di.createClipboardViewModel() }

    // Observa configuração para controlar modo de ativação
    val appConfig by viewModel.appConfig.collectAsState()

    // Estado para controlar qual janela está aberta
    var showMainWindow by remember { mutableStateOf(false) }

    // Determina se deve mostrar o mini indicador baseado na configuração
    val showMiniIndicator = appConfig.activationMode == info.arthurribeiro.pastecopy.domain.model.ActivationMode.PERSISTENT_ICON ||
                            appConfig.activationMode == info.arthurribeiro.pastecopy.domain.model.ActivationMode.BOTH

    // Determina se deve habilitar atalho de teclado baseado na configuração
    val enableKeyboardShortcut = appConfig.activationMode == info.arthurribeiro.pastecopy.domain.model.ActivationMode.KEYBOARD_SHORTCUT ||
                                  appConfig.activationMode == info.arthurribeiro.pastecopy.domain.model.ActivationMode.BOTH

    // Window state para a janela principal
    val mainWindowState = rememberWindowState(
        placement = WindowPlacement.Floating,
        position = WindowPosition(Alignment.BottomCenter),
        size = DpSize((Toolkit.getDefaultToolkit().screenSize.width - 100).dp, 200.dp)
    )

    // Window state para o mini indicador
    val miniWindowState = rememberWindowState(
        placement = WindowPlacement.Floating,
        position = WindowPosition(Alignment.BottomCenter),
        size = DpSize(120.dp, 50.dp)
    )

    // Configura posição inicial da janela principal (parte inferior)
    LaunchedEffect(Unit) {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val windowWidth = screenSize.width
        val windowHeight = 400

        mainWindowState.position = WindowPosition(
            x = 50.dp,
            y = (screenSize.height - 370).dp // 110px de margem inferior
        )
    }

    // Mini indicador sempre visível
    if (showMiniIndicator) {
        Window(
            onCloseRequest = { /* Não permite fechar, fica sempre visível */ },
            state = miniWindowState,
            title = "",
            alwaysOnTop = true,
            undecorated = true,
            transparent = true,
            resizable = false,
            focusable = false
        ) {
            MiniIndicatorApp(
                viewModel = viewModel,
                onOpenMain = {
                    showMainWindow = !showMainWindow
                }
            )
        }
    }

    // Janela principal (histórico completo)
    if (showMainWindow) {
        Window(
            onCloseRequest = { showMainWindow = false },
            state = mainWindowState,
            title = "PasteCopy",
            alwaysOnTop = true,
            undecorated = true,
            transparent = true,
            resizable = false
        ) {
            JvmApp(viewModel)
        }
    }
}