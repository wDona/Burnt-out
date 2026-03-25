package dev.wdona.burntout.shared.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.wdona.burntout.shared.db.AppDatabase
import java.io.File

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
        
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:$databasePath")

        val isNewDatabase = !databaseFile.exists()
        if (isNewDatabase) {
            AppDatabase.Schema.create(driver)
            val database = AppDatabase(driver)
            try {
                insertarDatosIniciales(database)
            } catch (e: Exception) {
                println("Error insertando datos iniciales: ${e.message}")
            }
        }

        return driver
    }

    private fun insertarDatosIniciales(database: AppDatabase) {
        database.appDatabaseQueries.insertOrgbase()
        database.appDatabaseQueries.insertEquipoBase()
        database.appDatabaseQueries.insertUsuarioBase()
        database.appDatabaseQueries.insertPreguntasBase()
    }
}
