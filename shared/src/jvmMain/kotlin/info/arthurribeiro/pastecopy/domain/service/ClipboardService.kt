package info.arthurribeiro.pastecopy.domain.service

import info.arthurribeiro.pastecopy.domain.model.AppConfig
import info.arthurribeiro.pastecopy.domain.model.ClipboardItem
import info.arthurribeiro.pastecopy.domain.model.ContentType
import info.arthurribeiro.pastecopy.domain.monitor.ClipboardMonitor
import info.arthurribeiro.pastecopy.domain.repository.ClipboardRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Serviço que coordena o monitoramento do clipboard e persistência no repository
 */
class ClipboardService(
    private val clipboardMonitor: ClipboardMonitor,
    private val repository: ClipboardRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) {
    private var monitoringJob: Job? = null

    /**
     * Inicia o serviço: monitora clipboard e salva automaticamente
     */
    fun start() {
        clipboardMonitor.start()

        monitoringJob = scope.launch {
            clipboardMonitor.observeClipboard().collect { content ->
                try {
                    val item = ClipboardItem(
                        id = UUID.randomUUID().toString(),
                        content = content,
                        timestamp = System.currentTimeMillis(),
                        isPinned = false,
                        type = ContentType.TEXT
                    )

                    repository.addItem(item)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Para o serviço
     */
    fun stop() {
        clipboardMonitor.stop()
        monitoringJob?.cancel()
        monitoringJob = null
    }

    /**
     * Observa todos os itens do clipboard
     */
    fun observeItems(): Flow<List<ClipboardItem>> {
        return repository.observeClipboardItems()
    }

    /**
     * Copia um item de volta para o clipboard
     */
    suspend fun copyItemToClipboard(item: ClipboardItem) {
        clipboardMonitor.copyToClipboard(item.content)
    }

    /**
     * Deleta um item do histórico
     */
    suspend fun deleteItem(itemId: String) {
        repository.deleteItem(itemId)
    }

    /**
     * Alterna o estado de fixado de um item
     */
    suspend fun togglePinItem(itemId: String) {
        repository.togglePinItem(itemId)
    }

    /**
     * Busca itens no histórico
     */
    suspend fun searchItems(query: String): List<ClipboardItem> {
        return repository.searchItems(query)
    }

    /**
     * Limpa o histórico
     */
    suspend fun clearHistory(keepPinned: Boolean = true) {
        repository.clearHistory(keepPinned)
    }

    /**
     * Obtém a configuração atual
     */
    suspend fun getConfig(): AppConfig {
        return repository.getConfig()
    }

    /**
     * Atualiza a configuração
     */
    suspend fun updateConfig(config: AppConfig) {
        repository.saveConfig(config)
    }
}
