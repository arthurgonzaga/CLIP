package info.arthurribeiro.pastecopy.platform

import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.NativeHookException
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Listener global de teclado para capturar atalhos do sistema
 * Usa JNativeHook para capturar teclas mesmo quando app não está em foco
 */
class GlobalKeyboardListener(
    private val onToggleVisibility: () -> Unit
) : NativeKeyListener {

    private var isShiftPressed = false
    private var isCtrlPressed = false
    private var isCmdPressed = false

    init {
        // Desabilita logs verbosos do JNativeHook
        Logger.getLogger(GlobalScreen::class.java.`package`.name).apply {
            level = Level.OFF
            useParentHandlers = false
        }
    }

    /**
     * Registra o listener global
     */
    fun register() {
        try {
            if (!GlobalScreen.isNativeHookRegistered()) {
                GlobalScreen.registerNativeHook()
            }
            GlobalScreen.addNativeKeyListener(this)
        } catch (e: NativeHookException) {
            System.err.println("Erro ao registrar listener global de teclado")
            e.printStackTrace()
        }
    }

    /**
     * Remove o listener global
     */
    fun unregister() {
        try {
            GlobalScreen.removeNativeKeyListener(this)
            if (GlobalScreen.isNativeHookRegistered()) {
                GlobalScreen.unregisterNativeHook()
            }
        } catch (e: NativeHookException) {
            e.printStackTrace()
        }
    }

    override fun nativeKeyPressed(event: NativeKeyEvent) {
        when (event.keyCode) {
            NativeKeyEvent.VC_SHIFT -> {
                isShiftPressed = true
            }
            NativeKeyEvent.VC_CONTROL -> {
                isCtrlPressed = true
            }
            NativeKeyEvent.VC_META -> {
                isCmdPressed = true
            }
            NativeKeyEvent.VC_V -> {
                // Detecta Cmd+Shift+V (Mac) ou Ctrl+Shift+V (Windows/Linux)
                if (isShiftPressed && (isCmdPressed || isCtrlPressed)) {
                    onToggleVisibility()
                }
            }
        }
    }

    override fun nativeKeyReleased(event: NativeKeyEvent) {
        when (event.keyCode) {
            NativeKeyEvent.VC_SHIFT -> {
                isShiftPressed = false
            }
            NativeKeyEvent.VC_CONTROL -> {
                isCtrlPressed = false
            }
            NativeKeyEvent.VC_META -> {
                isCmdPressed = false
            }
        }
    }

    override fun nativeKeyTyped(event: NativeKeyEvent) {
        // Não utilizado
    }
}
