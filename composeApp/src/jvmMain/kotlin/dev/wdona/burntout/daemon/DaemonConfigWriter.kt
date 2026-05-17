package dev.wdona.burntout.daemon

import dev.wdona.burntout.shared.db.sanitizeHost
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object DaemonConfigWriter {
    private val configFile
        get() = File(System.getProperty("user.home"), ".burntout_app/daemon-config.json")

    fun write(idUsuario: Long, notificacionesActivas: Boolean, host: String) {
        try {
            val dbPath = File(
                System.getProperty("user.home"),
                ".burntout_app/burntout_${sanitizeHost(host)}.db"
            ).absolutePath
            val config = DaemonConfig(idUsuario, dbPath, notificacionesActivas, host)
            configFile.writeText(Json.encodeToString(config))
        } catch (e: Exception) {
            println("[BurntOut] Error escribiendo config del daemon: ${e.message}")
        }
    }
}
