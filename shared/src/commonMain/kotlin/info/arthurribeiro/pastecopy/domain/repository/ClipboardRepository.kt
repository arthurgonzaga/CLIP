package info.arthurribeiro.pastecopy.domain.repository

import info.arthurribeiro.pastecopy.domain.model.AppConfig
import info.arthurribeiro.pastecopy.domain.model.ClipboardItem
import kotlinx.coroutines.flow.Flow

/**
 * Contrato para persistência de itens do clipboard e configurações
 */
interface ClipboardRepository {
    /**
     * Observa mudanças no histórico de itens copiados
     */
    fun observeClipboardItems(): Flow<List<ClipboardItem>>

    /**
     * Obtém todos os itens do histórico
     */
    suspend fun getAllItems(): List<ClipboardItem>

    /**
     * Adiciona um novo item ao histórico
     * @return true se foi adicionado, false se já existia (duplicata)
     */
    suspend fun addItem(item: ClipboardItem): Boolean

    /**
     * Remove um item do histórico
     */
    suspend fun deleteItem(itemId: String)

    /**
     * Alterna o estado de fixado de um item
     */
    suspend fun togglePinItem(itemId: String)

    /**
     * Busca itens no histórico por texto
     */
    suspend fun searchItems(query: String): List<ClipboardItem>

    /**
     * Limpa todo o histórico (mantém itens fixados)
     */
    suspend fun clearHistory(keepPinned: Boolean = true)

    /**
     * Salva as configurações do app
     */
    suspend fun saveConfig(config: AppConfig)

    /**
     * Carrega as configurações do app
     */
    suspend fun getConfig(): AppConfig
}
