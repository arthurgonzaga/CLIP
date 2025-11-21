package info.arthurribeiro.pastecopy.data.repository

import info.arthurribeiro.pastecopy.data.storage.JsonStorageManager
import info.arthurribeiro.pastecopy.domain.model.AppConfig
import info.arthurribeiro.pastecopy.domain.model.ClipboardItem
import info.arthurribeiro.pastecopy.domain.repository.ClipboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

/**
 * Implementação do ClipboardRepository usando JSON para persistência local (JVM)
 */
class ClipboardRepositoryImpl(
    private val storageManager: JsonStorageManager = JsonStorageManager()
) : ClipboardRepository {

    private val _clipboardItems = MutableStateFlow<List<ClipboardItem>>(emptyList())
    private val _config = MutableStateFlow(AppConfig())

    init {
        // Carrega dados persistidos ao inicializar
        loadFromStorage()
    }

    override fun observeClipboardItems(): Flow<List<ClipboardItem>> {
        return _clipboardItems.asStateFlow()
    }

    override suspend fun getAllItems(): List<ClipboardItem> {
        return _clipboardItems.value
    }

    override suspend fun addItem(item: ClipboardItem): Boolean {
        val currentItems = _clipboardItems.value

        // Evita duplicatas: verifica se o último item tem o mesmo conteúdo
        if (currentItems.isNotEmpty() && currentItems.first().content == item.content) {
            return false
        }

        // Adiciona no início da lista (mais recente primeiro)
        var updatedItems = listOf(item) + currentItems

        // Aplica limite de histórico (não remove itens fixados)
        val maxLimit = _config.value.maxHistoryItems
        if (maxLimit != Int.MAX_VALUE) {
            val pinnedItems = updatedItems.filter { it.isPinned }
            val unpinnedItems = updatedItems.filter { !it.isPinned }.take(maxLimit)
            updatedItems = (pinnedItems + unpinnedItems).sortedWith(
                compareByDescending<ClipboardItem> { it.isPinned }
                    .thenByDescending { it.timestamp }
            )
        }

        _clipboardItems.value = updatedItems

        saveToStorage()
        return true
    }

    override suspend fun deleteItem(itemId: String) {
        val updatedItems = _clipboardItems.value.filter { it.id != itemId }
        _clipboardItems.value = updatedItems
        saveToStorage()
    }

    override suspend fun togglePinItem(itemId: String) {
        val updatedItems = _clipboardItems.value.map { item ->
            if (item.id == itemId) {
                item.copy(isPinned = !item.isPinned)
            } else {
                item
            }
        }

        // Reorganiza: itens fixados primeiro, depois por timestamp
        val sortedItems = updatedItems.sortedWith(
            compareByDescending<ClipboardItem> { it.isPinned }
                .thenByDescending { it.timestamp }
        )

        _clipboardItems.value = sortedItems
        saveToStorage()
    }

    override suspend fun searchItems(query: String): List<ClipboardItem> {
        if (query.isBlank()) return _clipboardItems.value

        return _clipboardItems.value.filter { item ->
            item.content.contains(query, ignoreCase = true)
        }
    }

    override suspend fun clearHistory(keepPinned: Boolean) {
        val updatedItems = if (keepPinned) {
            _clipboardItems.value.filter { it.isPinned }
        } else {
            emptyList()
        }

        _clipboardItems.value = updatedItems
        saveToStorage()
    }

    override suspend fun saveConfig(config: AppConfig) {
        _config.value = config
        storageManager.save(CONFIG_FILE_NAME, config)
    }

    override suspend fun getConfig(): AppConfig {
        return _config.value
    }

    /**
     * Salva os itens do clipboard no armazenamento JSON
     */
    private fun saveToStorage() {
        val data = ClipboardData(items = _clipboardItems.value)
        storageManager.save(CLIPBOARD_FILE_NAME, data)
    }

    /**
     * Carrega os itens do clipboard e config do armazenamento JSON
     */
    private fun loadFromStorage() {
        // Carrega itens do clipboard
        val data = storageManager.load<ClipboardData>(CLIPBOARD_FILE_NAME)
        if (data != null) {
            _clipboardItems.value = data.items
        }

        // Carrega configurações
        val config = storageManager.load<AppConfig>(CONFIG_FILE_NAME)
        if (config != null) {
            _config.value = config
        }
    }

    companion object {
        private const val CLIPBOARD_FILE_NAME = "clipboard_history.json"
        private const val CONFIG_FILE_NAME = "app_config.json"
    }
}

/**
 * Wrapper para serialização da lista de ClipboardItems
 */
@Serializable
private data class ClipboardData(
    val items: List<ClipboardItem>
)
