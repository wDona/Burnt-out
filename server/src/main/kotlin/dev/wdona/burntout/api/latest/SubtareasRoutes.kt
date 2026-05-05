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
            val idTarea = call.request.queryParameters["idTarea"]
            println("[${call.request.origin.remoteHost}] GET /subtareas idTarea=$idTarea")
            val resultado = dbQuery {
                val query = if (idTarea != null) {
                    SubtareasTable .selectAll().where { (SubtareasTable.idTarea eq idTarea) and (SubtareasTable.isDeleted eq false) }
                } else {
                    SubtareasTable.selectAll().where { SubtareasTable.isDeleted eq false }
                }
                query.map {
                    Subtarea(
                        idSubtarea = it[SubtareasTable.id],
                        titulo = it[SubtareasTable.titulo],
                        descripcion = it[SubtareasTable.descripcion],
                        completado = it[SubtareasTable.completado],
                        idTareaPerteneciente = it[SubtareasTable.idTarea],
                        isDeleted = it[SubtareasTable.isDeleted]
                    )
                }
            }
            call.respond(resultado)
        }
        post {
            val subtarea = call.receive<Subtarea>()
            println("[${call.request.origin.remoteHost}] POST /subtareas titulo=${subtarea.titulo}")
            dbQuery {
                SubtareasTable.insert {
                    it[id] = subtarea.idSubtarea
                    it[titulo] = subtarea.titulo
                    it[descripcion] = subtarea.descripcion
                    it[completado] = subtarea.completado
                    it[idTarea] = subtarea.idTareaPerteneciente
                    it[isDeleted] = subtarea.isDeleted
                    it[updatedAt] = System.currentTimeMillis() / 1000
                }
            }
            call.respond(HttpStatusCode.Created, subtarea)
        }
        route("/{id}") {
            get {
                val id = call.parameters["id"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                println("[${call.request.origin.remoteHost}] GET /subtareas/$id")
                val subtarea = dbQuery {
                    SubtareasTable .selectAll().where { (SubtareasTable.id eq id) and (SubtareasTable.isDeleted eq false) }.map {
                        Subtarea(
                            idSubtarea = it[SubtareasTable.id],
                            titulo = it[SubtareasTable.titulo],
                            descripcion = it[SubtareasTable.descripcion],
                            completado = it[SubtareasTable.completado],
                            idTareaPerteneciente = it[SubtareasTable.idTarea],
                            isDeleted = it[SubtareasTable.isDeleted]
                        )
                    }.singleOrNull()
                } ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(subtarea)
            }
            patch {
                val id = call.parameters["id"]
                    ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<CompletadoRequest>()
                println("[${call.request.origin.remoteHost}] PATCH /subtareas/$id completado=${request.completado}")
                val isSuccess = dbQuery {
                    val count = SubtareasTable.update({ (SubtareasTable.id eq id) and (SubtareasTable.isDeleted eq false) }) {
                        it[completado] = request.completado
                        it[updatedAt] = System.currentTimeMillis() / 1000
                    }
                    count > 0
                }
                if (!isSuccess) return@patch call.respond(HttpStatusCode.NotFound)
                val updatedSubtarea = dbQuery {
                    SubtareasTable .selectAll().where { (SubtareasTable.id eq id) and (SubtareasTable.isDeleted eq false) }.map {
                        Subtarea(
                            idSubtarea = it[SubtareasTable.id],
                            titulo = it[SubtareasTable.titulo],
                            descripcion = it[SubtareasTable.descripcion],
                            completado = it[SubtareasTable.completado],
                            idTareaPerteneciente = it[SubtareasTable.idTarea],
                            isDeleted = it[SubtareasTable.isDeleted]
                        )
                    }.single()
                }
                call.respond(updatedSubtarea)
            }
            delete {
                val id = call.parameters["id"]
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)
                println("[${call.request.origin.remoteHost}] DELETE /subtareas/$id")
                val deletedCount = dbQuery {
                    SubtareasTable.update({ (SubtareasTable.id eq id) and (SubtareasTable.isDeleted eq false) }) {
                        it[isDeleted] = true
                    }
                }
                if (deletedCount == 0) return@delete call.respond(HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
