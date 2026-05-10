package dev.wdona.burntout.shared.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.wdona.burntout.shared.utils.SettingsManager
import java.io.File
import java.sql.DriverManager

actual fun eliminarBaseDatosLocal(): Boolean {
    return try {
        val host = SettingsManager.getHostActual()
        DatabaseActions.cerrarDriver()
        val userHome = System.getProperty("user.home")
        val dbFile = File(userHome, ".burntout_app/burntout_${sanitizeHost(host)}.db")
        val deleted = dbFile.delete()
        kotlin.system.exitProcess(0)
        deleted
    } catch (e: Exception) {
        println("Error al eliminar base de datos: ${e.message}")
        false
    }
}

actual class DatabaseDriverFactory {
    companion object {
        private val driverMap = mutableMapOf<String, SqlDriver>()
    }

    actual fun createDriver(): SqlDriver {
        val host = SettingsManager.getHostActual()
        driverMap[host]?.let { return it }

        val userHome = System.getProperty("user.home")
        val appDataDir = File(userHome, ".burntout_app")
        if (!appDataDir.exists()) {
            val created = appDataDir.mkdirs()
            if (!created) {
                println("Fallo al crear directorio de la app en ${appDataDir.absolutePath}")
            }
        }
        val finalDir = if (appDataDir.exists() && appDataDir.isDirectory) appDataDir else File(".")

        val dbName = "burntout_${sanitizeHost(host)}.db"
        val databaseFile = File(finalDir, dbName)
        val databasePath = databaseFile.absolutePath
        val isNewDatabase = !databaseFile.exists()
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:$databasePath")

        if (isNewDatabase) {
            AppDatabase.Schema.create(driver)
            val database = AppDatabase(driver)
            try {
                insertarDatosIniciales(database)
            } catch (e: Exception) {
                println("Error insertando datos iniciales: ${e.message}")
            }
        } else {
            aplicarMigracionesFaltantes(databasePath)
        }

        driverMap[host] = driver
        return driver
    }

    private fun aplicarMigracionesFaltantes(databasePath: String) {
        val migraciones = listOf(
            "ALTER TABLE TareaEntity ADD COLUMN Notificacion_Personalizada INTEGER"
        )
        try {
            DriverManager.getConnection("jdbc:sqlite:$databasePath").use { conn ->
                migraciones.forEach { sql ->
                    try {
                        conn.createStatement().use { it.execute(sql) }
                        println("[DB Migration] Aplicada: $sql")
                    } catch (_: Exception) {
                        // Columna ya existe, ignorar
                    }
                }
            }
        } catch (e: Exception) {
            println("[DB Migration] Error: ${e.message}")
        }
    }

    private fun insertarDatosIniciales(database: AppDatabase) {
        database.appDatabaseQueries.insertOrgbase()
        database.appDatabaseQueries.insertEquipoBase()
        database.appDatabaseQueries.insertUsuarioBase()
        database.appDatabaseQueries.insertPreguntasBase()
    }
}
