package dev.wdona.burntout.api.latest
import dev.wdona.burntout.db.DatabaseFactory.dbQuery
import dev.wdona.burntout.db.tables.SubtareasTable
import dev.wdona.burntout.shared.domain.Subtarea
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.origin
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
@Serializable
internal data class CompletadoRequest(val completado: Boolean)
fun Route.subtareasRoutes() {
    route("/subtareas") {
        get {
            val idTarea = call.request.queryParameters["idTarea"]?.toLongOrNull()
            println("[${call.request.origin.remoteHost}] GET /subtareas idTarea=$idTarea")
            val resultado = dbQuery {
                val query = if (idTarea != null) {
                    SubtareasTable .selectAll().where { SubtareasTable.idTarea eq idTarea }
                } else {
                    SubtareasTable.selectAll()
                }
                query.map {
                    Subtarea(
                        idSubtarea = it[SubtareasTable.id],
                        titulo = it[SubtareasTable.titulo],
                        descripcion = it[SubtareasTable.descripcion],
                        completado = it[SubtareasTable.completado],
                        idTareaPerteneciente = it[SubtareasTable.idTarea]
                    )
                }
            }
            call.respond(resultado)
        }
        post {
            val subtarea = call.receive<Subtarea>()
            println("[${call.request.origin.remoteHost}] POST /subtareas titulo=${subtarea.titulo}")
            val nuevoId = dbQuery {
                SubtareasTable.insert {
                    it[titulo] = subtarea.titulo
                    it[descripcion] = subtarea.descripcion
                    it[completado] = subtarea.completado
                    it[idTarea] = subtarea.idTareaPerteneciente
                }[SubtareasTable.id]
            }
            call.respond(HttpStatusCode.Created, subtarea.copy(idSubtarea = nuevoId))
        }
        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                println("[${call.request.origin.remoteHost}] GET /subtareas/$id")
                val subtarea = dbQuery {
                    SubtareasTable .selectAll().where { SubtareasTable.id eq id }.map {
                        Subtarea(
                            idSubtarea = it[SubtareasTable.id],
                            titulo = it[SubtareasTable.titulo],
                            descripcion = it[SubtareasTable.descripcion],
                            completado = it[SubtareasTable.completado],
                            idTareaPerteneciente = it[SubtareasTable.idTarea]
                        )
                    }.singleOrNull()
                } ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(subtarea)
            }
            patch {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<CompletadoRequest>()
                println("[${call.request.origin.remoteHost}] PATCH /subtareas/$id completado=${request.completado}")
                val isSuccess = dbQuery {
                    val count = SubtareasTable.update({ SubtareasTable.id eq id }) {
                        it[completado] = request.completado
                    }
                    count > 0
                }
                if (!isSuccess) return@patch call.respond(HttpStatusCode.NotFound)
                val updatedSubtarea = dbQuery {
                    SubtareasTable .selectAll().where { SubtareasTable.id eq id }.map {
                        Subtarea(
                            idSubtarea = it[SubtareasTable.id],
                            titulo = it[SubtareasTable.titulo],
                            descripcion = it[SubtareasTable.descripcion],
                            completado = it[SubtareasTable.completado],
                            idTareaPerteneciente = it[SubtareasTable.idTarea]
                        )
                    }.single()
                }
                call.respond(updatedSubtarea)
            }
            delete {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)
                println("[${call.request.origin.remoteHost}] DELETE /subtareas/$id")
                val deletedCount = dbQuery {
                    SubtareasTable.deleteWhere { SubtareasTable.id eq id }
                }
                if (deletedCount == 0) return@delete call.respond(HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
