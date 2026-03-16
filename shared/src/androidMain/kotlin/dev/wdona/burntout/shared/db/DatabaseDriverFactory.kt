package dev.wdona.burntout.shared.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dev.wdona.burntout.shared.utils.SettingsManager
import dev.wdona.burntout.shared.db.AppDatabase
import java.io.File

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        val databasePath = "burnt_out.db"
        val dbFile = File(databasePath)
        val driver: SqlDriver = AndroidSqliteDriver(AppDatabase.Schema, context, databasePath)

        val database = AppDatabase(driver)
        if (database.appDatabaseQueries.getOrganizacionById(SettingsManager.getIdOrganizacionActual()).executeAsOneOrNull() == null) {
            insertarDatosIniciales(database)
        }

        return driver
    }

    private fun insertarDatosIniciales(database: AppDatabase) {
        database.appDatabaseQueries.insertOrgbase()
        database.appDatabaseQueries.insertEquipoBase()
        database.appDatabaseQueries.insertUsuarioBase()
    }
}