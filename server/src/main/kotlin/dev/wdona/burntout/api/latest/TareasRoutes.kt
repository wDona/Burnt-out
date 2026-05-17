package dev.wdona.burntout.api.latest

import dev.wdona.burntout.db.DatabaseFactory.dbQuery
import dev.wdona.burntout.db.tables.SubtareasTable
import dev.wdona.burntout.db.tables.TareasTable
import dev.wdona.burntout.shared.domain.Tarea
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.origin
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

@Serializable
internal data class EstadoRequest(val estado: String)

fun Route.tareasRoutes() {
    route("/tareas") {
        get {
            val idTablero = call.request.queryParameters["idTablero"]

            println("[${call.request.origin.remoteHost}] GET /tareas idTablero=$idTablero")

            call.respond(getTareas(idTablero))
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
                    it[notificacionPersonalizada] = tarea.notificacionPersonalizada
                    it[isDeleted] = tarea.isDeleted
                    it[updatedAt] = System.currentTimeMillis() / 1000
                }
            }

            call.respond(HttpStatusCode.Created, tarea)
        }

        route("/{id}") {
            get {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)

                println("[${call.request.origin.remoteHost}] GET /tareas/$id")

                val tarea = dbQuery { getTareaConSubtareas(id) } ?: return@get call.respond(HttpStatusCode.NotFound)

                call.respond(tarea)
            }

            put {
                val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
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
                        it[notificacionPersonalizada] = tarea.notificacionPersonalizada
                        it[isDeleted] = tarea.isDeleted
                        it[updatedAt] = System.currentTimeMillis() / 1000
                    }
                }

                if (updatedCount == 0) return@put call.respond(HttpStatusCode.NotFound)

                call.respond(tarea.copy(idTarea = id))
            }
            delete {
                val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)

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
                val id = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest)

                val request = call.receive<EstadoRequest>()

                println("[${call.request.origin.remoteHost}] PATCH /tareas/$id/estado estado=${request.estado}")

                val tarea = dbQuery {
                    val count = TareasTable.update({ (TareasTable.id eq id) and (TareasTable.isDeleted eq false) }) {
                        it[estado] = request.estado
                        it[updatedAt] = System.currentTimeMillis() / 1000
                    }

                    if (count > 0) getTareaConSubtareas(id) else null

                } ?: return@patch call.respond(HttpStatusCode.NotFound)

                call.respond(tarea)
            }
        }
    }
}

private fun ResultRow.toTarea(subtareas: List<String> = emptyList()) = Tarea(
    idTarea = this[TareasTable.id],
    titulo = this[TareasTable.titulo],
    descripcion = this[TareasTable.descripcion],
    estado = this[TareasTable.estado],
    idTableroPerteneciente = this[TareasTable.idTablero],
    idUsuarioAsignado = this[TareasTable.idUsuarioAsignado],
    idSubtareas = subtareas.ifEmpty { null },
    fechaVencimiento = this[TareasTable.fechaVencimiento],
    notificacionPersonalizada = this[TareasTable.notificacionPersonalizada],
    isDeleted = this[TareasTable.isDeleted]
)

private fun getTareaConSubtareas(id: String): Tarea? {
    val row = TareasTable.selectAll()
        .where { (TareasTable.id eq id) and (TareasTable.isDeleted eq false) }
        .singleOrNull() ?: return null

    val subtareas = SubtareasTable.selectAll()
        .where { SubtareasTable.idTarea eq id }
        .map { it[SubtareasTable.id] }

    return row.toTarea(subtareas)
}

private suspend fun getTareas(idTablero: String?) = dbQuery {
    val query = if (idTablero != null) {
        TareasTable.selectAll().where { (TareasTable.idTablero eq idTablero) and (TareasTable.isDeleted eq false) }
    } else {
        TareasTable.selectAll().where { TareasTable.isDeleted eq false }
    }

    query.map { row ->
        val subtareas = SubtareasTable.selectAll()
            .where { SubtareasTable.idTarea eq row[TareasTable.id] }
            .map { it[SubtareasTable.id] }

        row.toTarea(subtareas)
    }
}
