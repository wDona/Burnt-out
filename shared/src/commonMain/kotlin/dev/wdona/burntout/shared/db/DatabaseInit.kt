package dev.wdona.burntout.shared.db

import app.cash.sqldelight.db.SqlDriver
import dev.wdona.burntout.shared.utils.SettingsManager
import dev.wdona.burntout.shared.db.AppDatabase

object DatabaseInit {
    private var database: AppDatabase? = null

    fun init(driver: SqlDriver) {
        if (database == null) {
            database = AppDatabase(driver)
            try {
                // FIXME posiblemente crashee?
                database?.appDatabaseQueries?.getOrganizacionById(SettingsManager.getIdOrganizacionActual())?.executeAsOneOrNull()
            } catch (e: Exception) {
            }
        }
    }

    fun getDatabase(): AppDatabase {
        return database ?: throw IllegalStateException("Database not initialized. Call init() first.")
    }
}
