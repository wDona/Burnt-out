package dev.wdona.burntout.shared.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.wdona.burntout.shared.db.AppDatabase
import java.io.File
import java.sql.DriverManager

actual fun eliminarBaseDatosLocal(): Boolean {
    return try {
        DatabaseActions.cerrarDriver()
        val userHome = System.getProperty("user.home")
        val dbFile = File(userHome, ".burntout_app/burntout.db")
        val deleted = dbFile.delete()
        kotlin.system.exitProcess(0)
        deleted
    } catch (e: Exception) {
        println("Error al eliminar base de datos: ${e.message}")
        false
    }
}

actual class DatabaseDriverFactory {

    // TODO: coger si existe el archivo o no o no
    actual fun createDriver(): SqlDriver {
        val userHome = System.getProperty("user.home")
        val appDataDir = File(userHome, ".burntout_app")
        
        if (!appDataDir.exists()) {
            val created = appDataDir.mkdirs()
            if (!created) {
                println("Fallo al crear el directorio de la app en ${appDataDir.absolutePath}, fallback en user.dir")
            }
        }
        
        val finalDir = if (appDataDir.exists() && appDataDir.isDirectory) appDataDir else File(".")

        val databaseFile = File(finalDir, "burntout.db")
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
