package dev.wdona.burntout.api.latest

import dev.wdona.burntout.db.DatabaseFactory.dbQuery
import dev.wdona.burntout.db.tables.AjustesTable
import dev.wdona.burntout.domain.model.Ajuste
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.origin
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

fun Route.ajustesRoutes() {
    route("/ajustes/{idUsuario}") {
        get {
            val idUsuario = call.parameters["idUsuario"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            println("[${call.request.origin.remoteHost}] GET /ajustes/$idUsuario")

            call.respond(getAjustesByUsuario(idUsuario))
        }

        post {
            val idUsuario = call.parameters["idUsuario"]?.toLongOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest)

            val ajuste = call.receive<Ajuste>()

            println("[${call.request.origin.remoteHost}] POST /ajustes/$idUsuario nombre=${ajuste.nombre}")

            call.respond(HttpStatusCode.Created, insertAjuste(idUsuario, ajuste))
        }
        route("/{id}") {
            put {
                val idUsuario = call.parameters["idUsuario"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)

                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)

                val ajuste = call.receive<Ajuste>()

                println("[${call.request.origin.remoteHost}] PUT /ajustes/$idUsuario/$id nombre=${ajuste.nombre}")

                val updated = updateAjuste(id, idUsuario, ajuste) ?: return@put call.respond(HttpStatusCode.NotFound)

                call.respond(updated)
            }
            delete {
                val idUsuario = call.parameters["idUsuario"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)

                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)

                println("[${call.request.origin.remoteHost}] DELETE /ajustes/$idUsuario/$id")

                if (!softDeleteAjuste(id, idUsuario)) return@delete call.respond(HttpStatusCode.NotFound)

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}

private fun ResultRow.toAjuste() = Ajuste(
    idAjuste = this[AjustesTable.id],
    nombre = this[AjustesTable.nombre],
    valorAjuste = this[AjustesTable.valorAjuste],
    isDeleted = this[AjustesTable.isDeleted],
    idUsuario = this[AjustesTable.idUsuario]
)

private suspend fun getAjustesByUsuario(idUsuario: Long) = dbQuery {
    AjustesTable.selectAll()
        .where { (AjustesTable.idUsuario eq idUsuario) and (AjustesTable.isDeleted eq false) }
        .map { it.toAjuste() }
}

private suspend fun insertAjuste(idUsuario: Long, ajuste: Ajuste): Ajuste {
    val now = System.currentTimeMillis() / 1000
    val nuevoId = dbQuery {
        AjustesTable.insert {
            it[AjustesTable.idUsuario] = idUsuario
            it[nombre] = ajuste.nombre
            it[valorAjuste] = ajuste.valorAjuste
            it[isDeleted] = ajuste.isDeleted
            it[updatedAt] = now
        }[AjustesTable.id]
    }
    return Ajuste(nuevoId, idUsuario, ajuste.nombre, ajuste.valorAjuste, ajuste.isDeleted, now)
}

private suspend fun updateAjuste(id: Long, idUsuario: Long, ajuste: Ajuste): Ajuste? {
    val now = System.currentTimeMillis() / 1000

    val count = dbQuery {
        AjustesTable.update({ (AjustesTable.id eq id) and (AjustesTable.idUsuario eq idUsuario) }) {
            it[nombre] = ajuste.nombre
            it[valorAjuste] = ajuste.valorAjuste
            it[isDeleted] = ajuste.isDeleted
            it[updatedAt] = now
        }
    }
    return if (count > 0) Ajuste(id, idUsuario, ajuste.nombre, ajuste.valorAjuste, ajuste.isDeleted, now) else null
}

private suspend fun softDeleteAjuste(id: Long, idUsuario: Long): Boolean = dbQuery {
    AjustesTable.update({
        (AjustesTable.id eq id) and (AjustesTable.idUsuario eq idUsuario) and (AjustesTable.isDeleted eq false)
    }) { it[isDeleted] = true } > 0
}
