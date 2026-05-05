package dev.wdona.burntout.api.latest
import dev.wdona.burntout.db.DatabaseFactory.dbQuery
import dev.wdona.burntout.db.tables.SubtareasTable
import dev.wdona.burntout.db.tables.TareasTable
import dev.wdona.burntout.shared.domain.Tarea
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.origin
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
@Serializable
internal data class EstadoRequest(val estado: String)
fun Route.tareasRoutes() {
    route("/tareas") {
        get {
            val idTablero = call.request.queryParameters["idTablero"]
            println("[${call.request.origin.remoteHost}] GET /tareas idTablero=$idTablero")
            val resultado = dbQuery {
                val query = if (idTablero != null) {
                    TareasTable .selectAll().where { (TareasTable.idTablero eq idTablero) and (TareasTable.isDeleted eq false) }
                } else {
                    TareasTable.selectAll().where { TareasTable.isDeleted eq false }
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
                        idSubtareas = subtareas.ifEmpty { null },
                        fechaVencimiento = row[TareasTable.fechaVencimiento],
                        isDeleted = row[TareasTable.isDeleted]
                    )
                }
            }
            call.respond(resultado)
        }
        post {
            val tarea = call.receive<Tarea>()
            println("[${call.request.origin.remoteHost}] POST /tareas titulo=${tarea.titulo}")
            dbQuery {
                TareasTable.insertIgnore {
                    it[id] = tarea.idTarea
                    it[titulo] = tarea.titulo
                    it[descripcion] = tarea.descripcion
                    it[estado] = tarea.estado
                    it[idTablero] = tarea.idTableroPerteneciente
                    it[idUsuarioAsignado] = tarea.idUsuarioAsignado
                    it[fechaVencimiento] = tarea.fechaVencimiento
                    it[isDeleted] = tarea.isDeleted
                    it[updatedAt] = System.currentTimeMillis() / 1000
                }
            }
            call.respond(HttpStatusCode.Created, tarea)
        }
        route("/{id}") {
            get {
                val id = call.parameters["id"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                println("[${call.request.origin.remoteHost}] GET /tareas/$id")
                val tarea = dbQuery {
                    val row = TareasTable .selectAll().where { (TareasTable.id eq id) and (TareasTable.isDeleted eq false) }.singleOrNull()
                    if (row != null) {
                        val subtareas = SubtareasTable .selectAll().where { SubtareasTable.idTarea eq id }.map { it[SubtareasTable.id] }
                        Tarea(
                            idTarea = row[TareasTable.id],
                            titulo = row[TareasTable.titulo],
                            descripcion = row[TareasTable.descripcion],
                            estado = row[TareasTable.estado],
                            idTableroPerteneciente = row[TareasTable.idTablero],
                            idUsuarioAsignado = row[TareasTable.idUsuarioAsignado],
                            idSubtareas = subtareas.ifEmpty { null },
                            fechaVencimiento = row[TareasTable.fechaVencimiento],
                            isDeleted = row[TareasTable.isDeleted]
                        )
                    } else null
                } ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(tarea)
            }
            put {
                val id = call.parameters["id"]
                    ?: return@put call.respond(HttpStatusCode.BadRequest)
                val tarea = call.receive<Tarea>()
                println("[${call.request.origin.remoteHost}] PUT /tareas/$id titulo=${tarea.titulo}")
                val updatedCount = dbQuery {
                    TareasTable.update({ TareasTable.id eq id }) {
                        it[titulo] = tarea.titulo
                        it[descripcion] = tarea.descripcion
                        it[estado] = tarea.estado
                        it[idTablero] = tarea.idTableroPerteneciente
                        it[idUsuarioAsignado] = tarea.idUsuarioAsignado
                        it[fechaVencimiento] = tarea.fechaVencimiento
                        it[isDeleted] = tarea.isDeleted
                        it[updatedAt] = System.currentTimeMillis() / 1000
                    }
                }
                if (updatedCount == 0) return@put call.respond(HttpStatusCode.NotFound)
                call.respond(tarea.copy(idTarea = id))
            }
            delete {
                val id = call.parameters["id"]
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)
                println("[${call.request.origin.remoteHost}] DELETE /tareas/$id")
                val deletedCount = dbQuery {
                    TareasTable.update({ (TareasTable.id eq id) and (TareasTable.isDeleted eq false) }) {
                        it[isDeleted] = true
                    }
                }
                if (deletedCount == 0) return@delete call.respond(HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.NoContent)
            }
            patch("/estado") {
                val id = call.parameters["id"]
                    ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<EstadoRequest>()
                println("[${call.request.origin.remoteHost}] PATCH /tareas/$id/estado estado=${request.estado}")
                val updatedTarea = dbQuery {
                    val count = TareasTable.update({ (TareasTable.id eq id) and (TareasTable.isDeleted eq false) }) {
                        it[estado] = request.estado
                        it[updatedAt] = System.currentTimeMillis() / 1000
                    }
                    if (count > 0) {
                        val row = TareasTable .selectAll().where { (TareasTable.id eq id) and (TareasTable.isDeleted eq false) }.single()
                        val subtareas = SubtareasTable .selectAll().where { SubtareasTable.idTarea eq id }.map { it[SubtareasTable.id] }
                        Tarea(
                            idTarea = row[TareasTable.id],
                            titulo = row[TareasTable.titulo],
                            descripcion = row[TareasTable.descripcion],
                            estado = row[TareasTable.estado],
                            idTableroPerteneciente = row[TareasTable.idTablero],
                            idUsuarioAsignado = row[TareasTable.idUsuarioAsignado],
                            idSubtareas = subtareas.ifEmpty { null },
                            fechaVencimiento = row[TareasTable.fechaVencimiento],
                            isDeleted = row[TareasTable.isDeleted]
                        )
                    } else null
                } ?: return@patch call.respond(HttpStatusCode.NotFound)
                call.respond(updatedTarea)
            }
        }
    }
}
