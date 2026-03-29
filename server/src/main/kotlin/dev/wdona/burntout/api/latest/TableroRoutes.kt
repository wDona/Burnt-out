package dev.wdona.burntout.api.latest
import dev.wdona.burntout.db.DatabaseFactory.dbQuery
import dev.wdona.burntout.db.tables.TablerosTable
import dev.wdona.burntout.shared.domain.Tablero
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.origin
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
fun Route.tablerosRoutes() {
    route("/tableros") {
        get {
            val idOrg = call.request.queryParameters["idOrg"]?.toLongOrNull()
            val idEquipo = call.request.queryParameters["idEquipo"]?.toLongOrNull()
            println("[${call.request.origin.remoteHost}] GET /tableros idOrg=$idOrg idEquipo=$idEquipo")
            val resultado = dbQuery {
                val query = TablerosTable.selectAll()
                if (idOrg != null) {
                    query.andWhere { TablerosTable.idOrganizacion eq idOrg }
                }
                if (idEquipo != null) {
                    query.andWhere { TablerosTable.idEquipo eq idEquipo }
                }
                query.map {
                    Tablero(
                        idTablero = it[TablerosTable.id],
                        titulo = it[TablerosTable.titulo],
                        idOrganizacion = it[TablerosTable.idOrganizacion],
                        idEquipo = it[TablerosTable.idEquipo]
                    )
                }
            }
            call.respond(resultado)
        }
        post {
            val tablero = call.receive<Tablero>()
            println("[${call.request.origin.remoteHost}] POST /tableros titulo=${tablero.titulo}")
            val nuevoId = dbQuery {
                TablerosTable.insert {
                    it[titulo] = tablero.titulo
                    it[idOrganizacion] = tablero.idOrganizacion
                    it[idEquipo] = tablero.idEquipo
                }[TablerosTable.id]
            }
            call.respond(HttpStatusCode.Created, tablero.copy(idTablero = nuevoId))
        }
        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                println("[${call.request.origin.remoteHost}] GET /tableros/$id")
                val tablero = dbQuery {
                    TablerosTable .selectAll().where { TablerosTable.id eq id }.map {
                        Tablero(
                            idTablero = it[TablerosTable.id],
                            titulo = it[TablerosTable.titulo],
                            idOrganizacion = it[TablerosTable.idOrganizacion],
                            idEquipo = it[TablerosTable.idEquipo]
                        )
                    }.singleOrNull()
                } ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(tablero)
            }
            put {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)
                val tablero = call.receive<Tablero>()
                println("[${call.request.origin.remoteHost}] PUT /tableros/$id titulo=${tablero.titulo}")
                val updatedCount = dbQuery {
                    TablerosTable.update({ TablerosTable.id eq id }) {
                        it[titulo] = tablero.titulo
                        it[idOrganizacion] = tablero.idOrganizacion
                        it[idEquipo] = tablero.idEquipo
                    }
                }
                if (updatedCount == 0) return@put call.respond(HttpStatusCode.NotFound)
                call.respond(tablero.copy(idTablero = id))
            }
            delete {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)
                println("[${call.request.origin.remoteHost}] DELETE /tableros/$id")
                val deletedCount = dbQuery {
                    TablerosTable.deleteWhere { TablerosTable.id eq id }
                }
                if (deletedCount == 0) return@delete call.respond(HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
