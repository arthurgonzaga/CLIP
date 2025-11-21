package info.arthurribeiro.pastecopy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.arthurribeiro.pastecopy.domain.model.ActivationMode
import info.arthurribeiro.pastecopy.domain.model.AppConfig
import info.arthurribeiro.pastecopy.domain.model.ClipboardItem
import info.arthurribeiro.pastecopy.domain.service.ClipboardService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para a tela de histórico do clipboard
 */
class ClipboardViewModel(
    private val clipboardService: ClipboardService
) : ViewModel() {

    private val _items = MutableStateFlow<List<ClipboardItem>>(emptyList())
    val items: StateFlow<List<ClipboardItem>> = _items.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _appConfig = MutableStateFlow(AppConfig())
    val appConfig: StateFlow<AppConfig> = _appConfig.asStateFlow()

    init {
        // Inicia o serviço de clipboard
        clipboardService.start()

        // Observa mudanças nos itens
        viewModelScope.launch {
            clipboardService.observeItems().collect { clipboardItems ->
                _items.value = clipboardItems
            }
        }

        // Carrega configuração inicial
        viewModelScope.launch {
            _appConfig.value = clipboardService.getConfig()
        }
    }

    /**
     * Copia um item de volta para o clipboard
     */
    fun copyItem(item: ClipboardItem) {
        viewModelScope.launch {
            clipboardService.copyItemToClipboard(item)
        }
    }

    /**
     * Deleta um item do histórico
     */
    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            clipboardService.deleteItem(itemId)
        }
    }

    /**
     * Alterna o estado de fixado de um item
     */
    fun togglePinItem(itemId: String) {
        viewModelScope.launch {
            clipboardService.togglePinItem(itemId)
        }
    }

    /**
     * Atualiza a query de busca
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                // Se a busca estiver vazia, observa todos os itens normalmente
                clipboardService.observeItems().collect { clipboardItems ->
                    _items.value = clipboardItems
                }
            } else {
                // Se houver query, busca itens
                val results = clipboardService.searchItems(query)
                _items.value = results
            }
        }
    }

    /**
     * Limpa o histórico
     */
    fun clearHistory(keepPinned: Boolean = true) {
        viewModelScope.launch {
            clipboardService.clearHistory(keepPinned)
        }
    }

    /**
     * Atualiza o modo de ativação
     */
    fun updateActivationMode(mode: ActivationMode) {
        viewModelScope.launch {
            val updatedConfig = _appConfig.value.copy(activationMode = mode)
            clipboardService.updateConfig(updatedConfig)
            _appConfig.value = updatedConfig
        }
    }

    /**
     * Atualiza o limite máximo de itens no histórico
     */
    fun updateMaxHistoryItems(limit: Int) {
        viewModelScope.launch {
            val updatedConfig = _appConfig.value.copy(maxHistoryItems = limit)
            clipboardService.updateConfig(updatedConfig)
            _appConfig.value = updatedConfig
        }
    }

    /**
     * Habilita/desabilita a busca rápida
     */
    fun updateQuickSearchEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val updatedConfig = _appConfig.value.copy(enableQuickSearch = enabled)
            clipboardService.updateConfig(updatedConfig)
            _appConfig.value = updatedConfig
        }
    }

    override fun onCleared() {
        super.onCleared()
        clipboardService.stop()
    }
}
