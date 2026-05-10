package dev.wdona.burntout.shared.db

import app.cash.sqldelight.db.SqlDriver
import dev.wdona.burntout.shared.utils.SettingsManager
import dev.wdona.burntout.shared.utils.getCurrentTimestampSeconds

fun sanitizeHost(host: String): String = host.replace(Regex("[^a-zA-Z0-9]"), "_")

object DatabaseActions {
    private val databases = mutableMapOf<String, AppDatabase>()
    private val drivers = mutableMapOf<String, SqlDriver>()
    var driverFactory: (() -> SqlDriver)? = null

    fun init(driver: SqlDriver) {
        val host = SettingsManager.getHostActual()
        if (databases.containsKey(host)) return

        val database = AppDatabase(driver)
        databases[host] = database
        drivers[host] = driver

        try {
            val queries = database.appDatabaseQueries
            queries.insertOrgbase()
            queries.insertEquipoBase()
            queries.insertPreguntasBase()
            queries.insertUsuarioBase()
            queries.insertUserTeam(Long.MIN_VALUE, Long.MIN_VALUE, getCurrentTimestampSeconds())

            val ayer = getCurrentTimestampSeconds() - 86400
            queries.insertRespuestasBase(
                idRespuesta = java.util.UUID.randomUUID().toString(),
                idUsuario = Long.MIN_VALUE,
                fecha = ayer
            )

            queries.getOrganizacionById(SettingsManager.getIdOrganizacionActual()).executeAsOneOrNull()
        } catch (e: Exception) {
            println("Error al inicializar la base de datos: ${e.message}")
        }
    }

    fun getDatabase(): AppDatabase {
        val host = SettingsManager.getHostActual()
        if (!databases.containsKey(host)) {
            val factory = driverFactory
                ?: throw IllegalStateException("Database not initialized for host '$host'. Call init() first.")
            init(factory())
        }
        return databases[host]!!
    }

    fun getDriver(): SqlDriver {
        val host = SettingsManager.getHostActual()
        return drivers[host]
            ?: throw IllegalStateException("Driver not initialized for host '$host'. Call init() first.")
    }

    fun recreateDB() {
        val host = SettingsManager.getHostActual()
        val database = databases[host] ?: return
        database.appDatabaseQueries.clearDB()
        databases.remove(host)
        val driver = drivers[host]!!
        init(driver)
    }

    fun cerrarDriver() {
        val host = SettingsManager.getHostActual()
        databases.remove(host)
        drivers[host]?.close()
        drivers.remove(host)
    }
}
