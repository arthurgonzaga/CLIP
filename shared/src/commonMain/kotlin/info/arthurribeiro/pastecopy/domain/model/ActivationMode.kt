package info.arthurribeiro.pastecopy.domain.model

/**
 * Modos de ativação da interface do PasteCopy
 */
enum class ActivationMode {
    /** Apenas atalho de teclado (Cmd+Shift+V / Ctrl+Shift+V) */
    KEYBOARD_SHORTCUT,

    /** Apenas ícone persistente na system tray */
    PERSISTENT_ICON,

    /** Ambos: atalho e ícone */
    BOTH
}
