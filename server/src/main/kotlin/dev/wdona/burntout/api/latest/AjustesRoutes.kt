package dev.wdona.burntout.api.latest
import dev.wdona.burntout.db.DatabaseFactory.dbQuery
import dev.wdona.burntout.db.tables.AjustesTable
import dev.wdona.burntout.domain.model.Ajuste
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.origin
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
fun Route.ajustesRoutes() {
    route("/ajustes/{idUsuario}") {
        get {
            val idUsuario = call.parameters["idUsuario"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)
            println("[${call.request.origin.remoteHost}] GET /ajustes/$idUsuario")
            val ajustes = dbQuery {
                AjustesTable .selectAll().where { (AjustesTable.idUsuario eq idUsuario) and (AjustesTable.isDeleted eq false) }.map {
                    Ajuste(
                        idAjuste = it[AjustesTable.id],
                        nombre = it[AjustesTable.nombre],
                        valorAjuste = it[AjustesTable.valorAjuste],
                        isDeleted = it[AjustesTable.isDeleted],
                        idUsuario = it[AjustesTable.idUsuario]
                    )
                }
            }
            call.respond(ajustes)
        }
        post {
            val idUsuario = call.parameters["idUsuario"]?.toLongOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest)
            val ajuste = call.receive<Ajuste>()
            println("[${call.request.origin.remoteHost}] POST /ajustes/$idUsuario nombre=${ajuste.nombre}")
            val nuevoId = dbQuery {
                AjustesTable.insert {
                    it[AjustesTable.idUsuario] = idUsuario
                    it[nombre] = ajuste.nombre
                    it[valorAjuste] = ajuste.valorAjuste
                    it[isDeleted] = ajuste.isDeleted
                }[AjustesTable.id]
            }
            call.respond(HttpStatusCode.Created, Ajuste(nuevoId, idUsuario, ajuste.nombre, ajuste.valorAjuste, ajuste.isDeleted))
        }
        route("/{id}") {
            put {
                val idUsuario = call.parameters["idUsuario"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)
                val ajuste = call.receive<Ajuste>()
                println("[${call.request.origin.remoteHost}] PUT /ajustes/$idUsuario/$id nombre=${ajuste.nombre}")
                val updatedCount = dbQuery {
                    AjustesTable.update({ (AjustesTable.id eq id) and (AjustesTable.idUsuario eq idUsuario) }) {
                        it[nombre] = ajuste.nombre
                        it[valorAjuste] = ajuste.valorAjuste
                        it[isDeleted] = ajuste.isDeleted
                    }
                }
                if (updatedCount == 0) return@put call.respond(HttpStatusCode.NotFound)
                call.respond(Ajuste(id, idUsuario, ajuste.nombre, ajuste.valorAjuste, ajuste.isDeleted))
            }
            delete {
                val idUsuario = call.parameters["idUsuario"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)
                println("[${call.request.origin.remoteHost}] DELETE /ajustes/$idUsuario/$id")
                val deletedCount = dbQuery {
                    AjustesTable.update({ (AjustesTable.id eq id) and (AjustesTable.idUsuario eq idUsuario) and (AjustesTable.isDeleted eq false) }) {
                        it[isDeleted] = true
                    }
                }
                if (deletedCount == 0) return@delete call.respond(HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
