package dev.wdona.burntout.daemon

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.wdona.burntout.platform.NotificacionProgramador
import dev.wdona.burntout.shared.db.AppDatabase
import kotlinx.serialization.json.Json
import java.io.File

object DaemonRunner {
    fun run() {
        println("[BurntOut Daemon] Iniciado (PID: ${ProcessHandle.current().pid()})")

        val configFile = File(System.getProperty("user.home"), ".burntout_app/daemon-config.json")
        val json = Json { ignoreUnknownKeys = true; isLenient = true }
        val programador = NotificacionProgramador()
        var lastConfigModified = 0L
        var lastConfig: DaemonConfig? = null

        while (true) {
            try {
                if (configFile.exists()) {
                    val modified = configFile.lastModified()

                    if (modified != lastConfigModified) {
                        lastConfigModified = modified
                        val config = try {
                            json.decodeFromString<DaemonConfig>(configFile.readText())
                        } catch (e: Exception) {
                            println("[BurntOut Daemon] Error leyendo config: ${e.message}")
                            null
                        }
                        if (config != null && config != lastConfig) {
                            lastConfig = config
                            reprogramar(config, programador)
                        }
                    }
                }
            } catch (e: Exception) {
                println("[BurntOut Daemon] Error en ciclo: ${e.message}")
            }

            Thread.sleep(30_000)
        }
    }

    private fun reprogramar(config: DaemonConfig, programador: NotificacionProgramador) {
        programador.cancelarTodasLasNotificaciones()

        if (!config.notificacionesActivas || config.idUsuario == Long.MIN_VALUE) {
            println("[BurntOut Daemon] Notificaciones inactivas o usuario no autenticado, saltando")
            return
        }

        val dbFile = File(config.dbPath)
        if (!dbFile.exists()) {
            println("[BurntOut Daemon] Base de datos no encontrada: ${config.dbPath}")
            return
        }

        try {
            val driver = JdbcSqliteDriver("jdbc:sqlite:${config.dbPath}")
            val database = AppDatabase(driver)
            val ahora = System.currentTimeMillis() / 1000

            val tareas = database.appDatabaseQueries
                .getTareasConFechaByUsuario(config.idUsuario)
                .executeAsList()
                .filter { it.Fecha_Vencimiento > ahora }

            driver.close()

            tareas.forEach { tarea ->
                val fecha = tarea.Fecha_Vencimiento
                programador.programarNotificaciones(
                    idTarea = tarea.ID_Tarea,
                    titulo = tarea.Titulo,
                    fechaVencimiento = fecha * 1000L,
                    notificacionPersonalizada = tarea.Notificacion_Personalizada?.let { it * 1000L }
                )
            }

            println("[BurntOut Daemon] ${tareas.size} notificaciones programadas para usuario ${config.idUsuario}")
        } catch (e: Exception) {
            println("[BurntOut Daemon] Error reprogramando: ${e.message}")
        }
    }
}
