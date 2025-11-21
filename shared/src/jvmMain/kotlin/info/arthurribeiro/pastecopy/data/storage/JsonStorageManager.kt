package info.arthurribeiro.pastecopy.data.storage

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Gerenciador de armazenamento JSON para JVM
 * Responsável por ler e escrever arquivos JSON no sistema de arquivos
 */
class JsonStorageManager(
    private val storageDir: File = getDefaultStorageDir()
) {
    internal val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    init {
        // Garante que o diretório de armazenamento existe
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
    }

    /**
     * Salva dados serializáveis em um arquivo JSON
     */
    internal inline fun <reified T> save(fileName: String, data: T) {
        val file = File(storageDir, fileName)
        val jsonString = json.encodeToString(data)
        file.writeText(jsonString)
    }

    /**
     * Carrega dados de um arquivo JSON
     * Retorna null se o arquivo não existir ou houver erro na deserialização
     */
    internal inline fun <reified T> load(fileName: String): T? {
        val file = File(storageDir, fileName)
        if (!file.exists()) return null

        return try {
            val jsonString = file.readText()
            json.decodeFromString<T>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Verifica se um arquivo existe
     */
    fun exists(fileName: String): Boolean {
        return File(storageDir, fileName).exists()
    }

    /**
     * Deleta um arquivo
     */
    fun delete(fileName: String): Boolean {
        val file = File(storageDir, fileName)
        return if (file.exists()) file.delete() else false
    }

    companion object {
        private const val APP_DIR_NAME = "PasteCopy"

        /**
         * Retorna o diretório padrão de armazenamento baseado no OS
         * Mac: ~/Library/Application Support/PasteCopy
         * Windows: %APPDATA%/PasteCopy
         * Linux: ~/.config/PasteCopy
         */
        private fun getDefaultStorageDir(): File {
            val userHome = System.getProperty("user.home")
            val osName = System.getProperty("os.name").lowercase()

            val baseDir = when {
                osName.contains("mac") || osName.contains("darwin") -> {
                    File(userHome, "Library/Application Support")
                }
                osName.contains("win") -> {
                    File(System.getenv("APPDATA") ?: "$userHome/AppData/Roaming")
                }
                else -> { // Linux e outros Unix-like
                    File(userHome, ".config")
                }
            }

            return File(baseDir, APP_DIR_NAME)
        }
    }
}
