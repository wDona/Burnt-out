package dev.wdona.burntout.shared.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dev.wdona.burntout.shared.utils.SettingsManager
import dev.wdona.burntout.shared.db.AppDatabase
import java.io.File

actual class DatabaseDriverFactory(private val context: Context) {
    companion object {
        internal var driverInstance: SqlDriver? = null
        private var appDatabaseInstance: AppDatabase? = null
        internal var storedContext: Context? = null

        fun resetDriver() {
            driverInstance?.close()
            driverInstance = null
            appDatabaseInstance = null
        }
    }

    actual fun createDriver(): SqlDriver {
        storedContext = context.applicationContext
        if (driverInstance == null) {
            val databasePath = "burntout.db"
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

actual fun eliminarBaseDatosLocal(): Boolean {
    val ctx = DatabaseDriverFactory.storedContext ?: return false
    return try {
        DatabaseActions.cerrarDriver()
        DatabaseDriverFactory.resetDriver()
        ctx.deleteDatabase("burntout.db")
    } catch (e: Exception) {
        println("Error al eliminar base de datos: ${e.message}")
        false
    }
}