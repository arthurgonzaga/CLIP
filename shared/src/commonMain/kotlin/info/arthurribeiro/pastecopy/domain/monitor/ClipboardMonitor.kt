package info.arthurribeiro.pastecopy.domain.monitor

import kotlinx.coroutines.flow.Flow

/**
 * Contrato para monitoramento do clipboard do sistema
 * Implementações específicas por plataforma (JVM, Android, iOS)
 */
interface ClipboardMonitor {
    /**
     * Observa mudanças no clipboard do sistema
     * Emite o conteúdo copiado quando houver mudança
     */
    fun observeClipboard(): Flow<String>

    /**
     * Inicia o monitoramento do clipboard
     */
    fun start()

    /**
     * Para o monitoramento do clipboard
     */
    fun stop()

    /**
     * Copia um texto para o clipboard
     */
    suspend fun copyToClipboard(text: String)

    /**
     * Obtém o conteúdo atual do clipboard
     */
    suspend fun getCurrentClipboardContent(): String?
}
