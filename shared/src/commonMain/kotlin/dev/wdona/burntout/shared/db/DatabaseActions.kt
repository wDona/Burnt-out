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
                val queries = database!!.appDatabaseQueries
                queries.insertOrgbase()
                queries.insertEquipoBase()
                queries.insertPreguntasBase()
                queries.insertUsuarioBase()
                queries.insertUserTeam(Long.MIN_VALUE, Long.MIN_VALUE)

                val ayer = getCurrentTimestampSeconds() - 86400
                queries.insertRespuestasBase(idUsuario = Long.MIN_VALUE, fecha = ayer)

                // FIXME posiblemente crashee?
                queries.getOrganizacionById(SettingsManager.getIdOrganizacionActual()).executeAsOneOrNull()
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
