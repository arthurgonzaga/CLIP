package info.arthurribeiro.pastecopy.domain.model

import kotlinx.serialization.Serializable

/**
 * Representa um item copiado no histórico do clipboard
 *
 * @property id Identificador único do item
 * @property content Conteúdo do item (texto no MVP)
 * @property timestamp Timestamp Unix em milissegundos de quando foi copiado
 * @property isPinned Se o item está fixado/favoritado
 * @property type Tipo de conteúdo (TEXT no MVP)
 */
@Serializable
data class ClipboardItem(
    val id: String,
    val content: String,
    val timestamp: Long,
    val isPinned: Boolean = false,
    val type: ContentType = ContentType.TEXT
)
