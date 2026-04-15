package dev.wdona.burntout.shared.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dev.wdona.burntout.shared.utils.SettingsManager
import dev.wdona.burntout.shared.db.AppDatabase
import java.io.File

actual class DatabaseDriverFactory(private val context: Context) {
    companion object {
        private var driverInstance: SqlDriver? = null
        private var appDatabaseInstance: AppDatabase? = null
    }

    actual fun createDriver(): SqlDriver {
        if (driverInstance == null) {
            val databasePath = "burntout.db"
            val dbFile = File(databasePath)
            driverInstance = AndroidSqliteDriver(AppDatabase.Schema, context, databasePath)
            
            appDatabaseInstance = AppDatabase(driverInstance!!)
            if (appDatabaseInstance!!.appDatabaseQueries.getOrganizacionById(SettingsManager.getIdOrganizacionActual()).executeAsOneOrNull() == null) {
                insertarDatosIniciales(appDatabaseInstance!!)
            }
        }

        return driverInstance!!
    }

    private fun insertarDatosIniciales(database: AppDatabase) {
        database.appDatabaseQueries.insertOrgbase()
        database.appDatabaseQueries.insertEquipoBase()
        database.appDatabaseQueries.insertUsuarioBase()
        database.appDatabaseQueries.insertPreguntasBase()
    }
}