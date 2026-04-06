package dev.wdona.burntout.shared.db

import app.cash.sqldelight.db.SqlDriver
import dev.wdona.burntout.shared.utils.SettingsManager

import dev.wdona.burntout.shared.utils.getCurrentTimestampSeconds

object DatabaseActions {
    private var database: AppDatabase? = null
    private var driver: SqlDriver? = null

    fun init(driver: SqlDriver) {
        if (database == null) {
            database = AppDatabase(driver)
            this.driver = driver

            try {
                // FIXME posiblemente crashee?
                database?.appDatabaseQueries?.getOrganizacionById(SettingsManager.getIdOrganizacionActual())?.executeAsOneOrNull()

                val ayer = getCurrentTimestampSeconds() - 86400
                database?.appDatabaseQueries?.insertRespuestasBase(
                    idUsuario = Long.MIN_VALUE,
                    fecha = ayer
                )
            } catch (e: Exception) {
                println("Error al inicializar la base de datos: ${e.message}")
            }
        }
    }

    fun getDatabase(): AppDatabase {
        return database ?: throw IllegalStateException("Database not initialized. Call init() first.")
    }

    fun getDriver(): SqlDriver {
        return driver ?: throw IllegalStateException("Driver not initialized. Call init() first.")
    }

    fun recreateDB() {
        database?.appDatabaseQueries?.clearDB()
        database = null

        val driver = driver!!
        init(driver)
    }
}
