package info.arthurribeiro.pastecopy.data.monitor

import info.arthurribeiro.pastecopy.domain.monitor.ClipboardMonitor
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.UnsupportedFlavorException

/**
 * Implementação do ClipboardMonitor para JVM (Desktop)
 * Monitora o clipboard do sistema usando polling
 */
class ClipboardMonitorImpl(
    private val pollingIntervalMs: Long = 500L, // 500ms
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) : ClipboardMonitor {

    private val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    private val _clipboardFlow = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 10)

    private var monitoringJob: Job? = null
    private var lastClipboardContent: String? = null
    private var isRunning = false

    override fun observeClipboard(): Flow<String> {
        return _clipboardFlow.asSharedFlow()
    }

    override fun start() {
        if (isRunning) return

        isRunning = true
        lastClipboardContent = getCurrentClipboardContentInternal()

        monitoringJob = scope.launch {
            while (isActive && isRunning) {
                try {
                    val currentContent = getCurrentClipboardContentInternal()

                    // Emite apenas se o conteúdo mudou e não é nulo/vazio
                    if (currentContent != null &&
                        currentContent.isNotBlank() &&
                        currentContent != lastClipboardContent) {

                        lastClipboardContent = currentContent
                        _clipboardFlow.emit(currentContent)
                    }
                } catch (e: Exception) {
                    // Ignora erros de acesso ao clipboard (pode acontecer se outro app estiver usando)
                    e.printStackTrace()
                }

                delay(pollingIntervalMs)
            }
        }
    }

    override fun stop() {
        isRunning = false
        monitoringJob?.cancel()
        monitoringJob = null
    }

    override suspend fun copyToClipboard(text: String) {
        withContext(Dispatchers.IO) {
            try {
                val selection = StringSelection(text)
                clipboard.setContents(selection, selection)
                // Atualiza o último conteúdo conhecido para evitar re-captura
                lastClipboardContent = text
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun getCurrentClipboardContent(): String? {
        return withContext(Dispatchers.IO) {
            getCurrentClipboardContentInternal()
        }
    }

    /**
     * Obtém o conteúdo atual do clipboard (versão interna sem suspend)
     */
    private fun getCurrentClipboardContentInternal(): String? {
        return try {
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                clipboard.getData(DataFlavor.stringFlavor) as? String
            } else {
                null
            }
        } catch (e: UnsupportedFlavorException) {
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
