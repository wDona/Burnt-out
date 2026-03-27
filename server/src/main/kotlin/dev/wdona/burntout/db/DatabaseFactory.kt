package dev.wdona.burntout.db
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import dev.wdona.burntout.db.tables.*
import org.jetbrains.exposed.sql.SchemaUtils

object DatabaseFactory {
    fun init() {
        Database.connect("jdbc:sqlite:server.db", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(
                OrganizacionesTable,
                EquiposTable,
                EquipoMiembrosTable,
                UsuariosTable,
                AjustesTable,
                TablerosTable,
                TareasTable,
                SubtareasTable,
                PreguntasTable,
                RespuestasTable
            )
        }
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}
