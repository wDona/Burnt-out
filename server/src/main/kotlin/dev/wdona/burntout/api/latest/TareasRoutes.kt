package dev.wdona.burntout.api.latest
import dev.wdona.burntout.db.DatabaseFactory.dbQuery
import dev.wdona.burntout.db.tables.SubtareasTable
import dev.wdona.burntout.db.tables.TareasTable
import dev.wdona.burntout.shared.domain.Tarea
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
@Serializable
private data class EstadoRequest(val estado: String)
fun Route.tareasRoutes() {
    route("/tareas") {
        get {
            val idTablero = call.request.queryParameters["idTablero"]?.toLongOrNull()
            val resultado = dbQuery {
                val query = if (idTablero != null) {
                    TareasTable .selectAll().where { TareasTable.idTablero eq idTablero }
                } else {
                    TareasTable.selectAll()
                }
                query.map { row ->
                    val tId = row[TareasTable.id]
                    val subtareas = SubtareasTable .selectAll().where { SubtareasTable.idTarea eq tId }.map { it[SubtareasTable.id] }
                    Tarea(
                        idTarea = tId,
                        titulo = row[TareasTable.titulo],
                        descripcion = row[TareasTable.descripcion],
                        estado = row[TareasTable.estado],
                        idTableroPerteneciente = row[TareasTable.idTablero],
                        idUsuarioAsignado = row[TareasTable.idUsuarioAsignado],
                        idSubtareas = subtareas.ifEmpty { null }
                    )
                }
            }
            call.respond(resultado)
        }
        post {
            val tarea = call.receive<Tarea>()
            val nuevoId = dbQuery {
                TareasTable.insert {
                    it[titulo] = tarea.titulo
                    it[descripcion] = tarea.descripcion
                    it[estado] = tarea.estado
                    it[idTablero] = tarea.idTableroPerteneciente
                    it[idUsuarioAsignado] = tarea.idUsuarioAsignado
                }[TareasTable.id]
            }
            call.respond(HttpStatusCode.Created, tarea.copy(idTarea = nuevoId))
        }
        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                val tarea = dbQuery {
                    val row = TareasTable .selectAll().where { TareasTable.id eq id }.singleOrNull()
                    if (row != null) {
                        val subtareas = SubtareasTable .selectAll().where { SubtareasTable.idTarea eq id }.map { it[SubtareasTable.id] }
                        Tarea(
                            idTarea = row[TareasTable.id],
                            titulo = row[TareasTable.titulo],
                            descripcion = row[TareasTable.descripcion],
                            estado = row[TareasTable.estado],
                            idTableroPerteneciente = row[TareasTable.idTablero],
                            idUsuarioAsignado = row[TareasTable.idUsuarioAsignado],
                            idSubtareas = subtareas.ifEmpty { null }
                        )
                    } else null
                } ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(tarea)
            }
            put {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)
                val tarea = call.receive<Tarea>()
                val updatedCount = dbQuery {
                    TareasTable.update({ TareasTable.id eq id }) {
                        it[titulo] = tarea.titulo
                        it[descripcion] = tarea.descripcion
                        it[estado] = tarea.estado
                        it[idTablero] = tarea.idTableroPerteneciente
                        it[idUsuarioAsignado] = tarea.idUsuarioAsignado
                    }
                }
                if (updatedCount == 0) return@put call.respond(HttpStatusCode.NotFound)
                call.respond(tarea.copy(idTarea = id))
            }
            delete {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)
                val deletedCount = dbQuery {
                    TareasTable.deleteWhere { TareasTable.id eq id }
                }
                if (deletedCount == 0) return@delete call.respond(HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.NoContent)
            }
            patch("/estado") {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<EstadoRequest>()
                val updatedTarea = dbQuery {
                    val count = TareasTable.update({ TareasTable.id eq id }) {
                        it[estado] = request.estado
                    }
                    if (count > 0) {
                        val row = TareasTable .selectAll().where { TareasTable.id eq id }.single()
                        val subtareas = SubtareasTable .selectAll().where { SubtareasTable.idTarea eq id }.map { it[SubtareasTable.id] }
                        Tarea(
                            idTarea = row[TareasTable.id],
                            titulo = row[TareasTable.titulo],
                            descripcion = row[TareasTable.descripcion],
                            estado = row[TareasTable.estado],
                            idTableroPerteneciente = row[TareasTable.idTablero],
                            idUsuarioAsignado = row[TareasTable.idUsuarioAsignado],
                            idSubtareas = subtareas.ifEmpty { null }
                        )
                    } else null
                } ?: return@patch call.respond(HttpStatusCode.NotFound)
                call.respond(updatedTarea)
            }
        }
    }
}
