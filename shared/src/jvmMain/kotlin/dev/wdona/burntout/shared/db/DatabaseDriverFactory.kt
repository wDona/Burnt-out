package dev.wdona.burntout.shared.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.wdona.burntout.shared.db.AppDatabase
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val userHome = System.getProperty("user.home")
        val appDataDir = File(userHome, ".burnt_out_app")
        
        if (!appDataDir.exists()) {
            appDataDir.mkdirs()
        }

        val databaseFile = File(appDataDir, "burnt_out.db")
        val databasePath = databaseFile.absolutePath
        
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:$databasePath")

        val isNewDatabase = !databaseFile.exists()
        if (isNewDatabase) {
            AppDatabase.Schema.create(driver)
            val database = AppDatabase(driver)
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
