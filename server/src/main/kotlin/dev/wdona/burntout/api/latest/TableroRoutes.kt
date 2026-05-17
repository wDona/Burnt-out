package dev.wdona.burntout.api.latest

import dev.wdona.burntout.db.DatabaseFactory.dbQuery
import dev.wdona.burntout.db.tables.TablerosTable
import dev.wdona.burntout.shared.domain.Tablero
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
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

fun Route.tablerosRoutes() {
    route("/tableros") {
        get {
            val idOrg = call.request.queryParameters["idOrg"]?.toLongOrNull()
            val idEquipo = call.request.queryParameters["idEquipo"]?.toLongOrNull()

            println("[${call.request.origin.remoteHost}] GET /tableros idOrg=$idOrg idEquipo=$idEquipo")

            call.respond(getTableros(idOrg, idEquipo))
        }
        post {
            val tablero = call.receive<Tablero>()

            println("[${call.request.origin.remoteHost}] POST /tableros ->$tablero")

            dbQuery {
                TablerosTable.insert {
                    it[id] = tablero.idTablero
                    it[titulo] = tablero.titulo
                    it[idOrganizacion] = tablero.idOrganizacion
                    it[idEquipo] = tablero.idEquipo
                    it[isDeleted] = tablero.isDeleted
                    it[updatedAt] = System.currentTimeMillis() / 1000
                }
            }

            call.respond(HttpStatusCode.Created, tablero)
        }

        route("/{id}") {
            get {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)

                println("[${call.request.origin.remoteHost}] GET /tableros/$id")

                val tablero = dbQuery {
                    TablerosTable.selectAll()
                        .where { (TablerosTable.id eq id) and (TablerosTable.isDeleted eq false) }
                        .map { it.toTablero() }.singleOrNull()
                } ?: return@get call.respond(HttpStatusCode.NotFound)

                call.respond(tablero)
            }

            put {
                val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)

                val tablero = call.receive<Tablero>()

                println("[${call.request.origin.remoteHost}] PUT /tableros/$id titulo=${tablero.titulo}")

                val updatedCount = dbQuery {
                    TablerosTable.update({ TablerosTable.id eq id }) {
                        it[titulo] = tablero.titulo
                        it[idOrganizacion] = tablero.idOrganizacion
                        it[idEquipo] = tablero.idEquipo
                        it[isDeleted] = tablero.isDeleted
                        it[updatedAt] = System.currentTimeMillis() / 1000
                    }
                }

                if (updatedCount == 0) return@put call.respond(HttpStatusCode.NotFound)

                call.respond(tablero.copy(idTablero = id))
            }

            delete {
                val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)

                println("[${call.request.origin.remoteHost}] DELETE /tableros/$id")

                val deletedCount = dbQuery {
                    TablerosTable.update({ (TablerosTable.id eq id) and (TablerosTable.isDeleted eq false) }) {
                        it[isDeleted] = true
                    }
                }

                if (deletedCount == 0) return@delete call.respond(HttpStatusCode.NotFound)

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}

private fun ResultRow.toTablero() = Tablero(
    idTablero = this[TablerosTable.id],
    titulo = this[TablerosTable.titulo],
    idOrganizacion = this[TablerosTable.idOrganizacion],
    idEquipo = this[TablerosTable.idEquipo],
    isDeleted = this[TablerosTable.isDeleted]
)

private suspend fun getTableros(idOrg: Long?, idEquipo: Long?) = dbQuery {
    val query = TablerosTable.selectAll().where { TablerosTable.isDeleted eq false }
    if (idOrg != null) query.andWhere { TablerosTable.idOrganizacion eq idOrg }
    if (idEquipo != null) query.andWhere { TablerosTable.idEquipo eq idEquipo }
    query.map { it.toTablero() }
}
