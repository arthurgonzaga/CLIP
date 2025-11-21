package info.arthurribeiro.pastecopy.domain.model

/**
 * Tipos de conteúdo suportados pelo clipboard
 * MVP: apenas TEXT
 * Futuro: IMAGE, URL, FILE
 */
enum class ContentType {
    TEXT,
    IMAGE,   // Futuro
    URL,     // Futuro
    FILE     // Futuro
}
