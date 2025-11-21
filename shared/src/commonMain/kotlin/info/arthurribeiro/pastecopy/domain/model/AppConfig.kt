package info.arthurribeiro.pastecopy.domain.model

import kotlinx.serialization.Serializable

/**
 * Configurações do aplicativo
 *
 * @property activationMode Modo de ativação da interface (atalho, ícone, ou ambos)
 * @property maxHistoryItems Máximo de itens no histórico (ilimitado no MVP)
 * @property enableQuickSearch Se a busca rápida está habilitada
 */
@Serializable
data class AppConfig(
    val activationMode: ActivationMode = ActivationMode.BOTH,
    val maxHistoryItems: Int = Int.MAX_VALUE, // Ilimitado (premium)
    val enableQuickSearch: Boolean = true
)
