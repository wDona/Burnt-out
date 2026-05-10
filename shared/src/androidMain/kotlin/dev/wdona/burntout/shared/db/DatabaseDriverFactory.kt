package dev.wdona.burntout.shared.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dev.wdona.burntout.shared.utils.SettingsManager

actual class DatabaseDriverFactory(private val context: Context) {
    companion object {
        private val driverMap = mutableMapOf<String, SqlDriver>()
        private val dbMap = mutableMapOf<String, AppDatabase>()
        internal var storedContext: Context? = null

        fun resetDriver(host: String? = null) {
            if (host != null) {
                driverMap[host]?.close()
                driverMap.remove(host)
                dbMap.remove(host)
            } else {
                driverMap.values.forEach { it.close() }
                driverMap.clear()
                dbMap.clear()
            }
        }
    }

    actual fun createDriver(): SqlDriver {
        storedContext = context.applicationContext
        val host = SettingsManager.getHostActual()

        if (!driverMap.containsKey(host)) {
            val dbName = "burntout_${sanitizeHost(host)}.db"
            val driver = AndroidSqliteDriver(AppDatabase.Schema, context, dbName)
            driverMap[host] = driver

            val database = AppDatabase(driver)
            dbMap[host] = database
            if (database.appDatabaseQueries
                    .getOrganizacionById(SettingsManager.getIdOrganizacionActual())
                    .executeAsOneOrNull() == null
            ) {
                insertarDatosIniciales(database)
            }
        }

        return driverMap[host]!!
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
        val host = SettingsManager.getHostActual()
        DatabaseActions.cerrarDriver()
        DatabaseDriverFactory.resetDriver(host)
        val dbName = "burntout_${sanitizeHost(host)}.db"
        ctx.deleteDatabase(dbName)
    } catch (e: Exception) {
        println("Error al eliminar base de datos: ${e.message}")
        false
    }
}
