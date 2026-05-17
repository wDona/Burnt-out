package dev.wdona.burntout.api.latest
import dev.wdona.burntout.db.DatabaseFactory
import dev.wdona.burntout.db.DatabaseFactory.dbQuery
import dev.wdona.burntout.db.tables.OrganizacionesTable
import dev.wdona.burntout.shared.domain.Organizacion
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.origin
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*

fun Route.organizacionesRoutes() {
    route("/organizaciones") {
        get {
            println("[${call.request.origin.remoteHost}] GET /organizaciones")

            val organizaciones = dbQuery {
                OrganizacionesTable.selectAll().where { OrganizacionesTable.isDeleted eq false }.map {
                    Organizacion(
                        idOrganizacion = it[OrganizacionesTable.id],
                        nombre = it[OrganizacionesTable.nombre],
                        isDeleted = it[OrganizacionesTable.isDeleted]
                    )
                }
            }

            call.respond(organizaciones)
        }
        post {
            val organizacion = call.receive<Organizacion>()

            println("[${call.request.origin.remoteHost}] POST /organizaciones nombre=${organizacion.nombre}")

            val nuevaId = dbQuery {
                val id = OrganizacionesTable.insert {
                    it[nombre] = organizacion.nombre
                    it[isDeleted] = organizacion.isDeleted
                    it[updatedAt] = System.currentTimeMillis() / 1000
                }[OrganizacionesTable.id]

                DatabaseFactory.insertPreguntasMBI(id)
                id
            }

            call.respond(HttpStatusCode.Created, organizacion.copy(idOrganizacion = nuevaId))
        }

        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)

                println("[${call.request.origin.remoteHost}] GET /organizaciones/$id")

                val organizacion = dbQuery {
                    OrganizacionesTable .selectAll().where { (OrganizacionesTable.id eq id) and (OrganizacionesTable.isDeleted eq false) }
                        .map {
                            Organizacion(
                                idOrganizacion = it[OrganizacionesTable.id],
                                nombre = it[OrganizacionesTable.nombre],
                                isDeleted = it[OrganizacionesTable.isDeleted]
                            )
                        }
                        .singleOrNull()
                } ?: return@get call.respond(HttpStatusCode.NotFound)

                call.respond(organizacion)
            }
            put {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)

                val organizacion = call.receive<Organizacion>()

                println("[${call.request.origin.remoteHost}] PUT /organizaciones/$id nombre=${organizacion.nombre}")

                val updatedCount = dbQuery {
                    OrganizacionesTable.update({ OrganizacionesTable.id eq id }) {
                        it[nombre] = organizacion.nombre
                        it[isDeleted] = organizacion.isDeleted
                        it[updatedAt] = System.currentTimeMillis() / 1000
                    }
                }

                if (updatedCount == 0) return@put call.respond(HttpStatusCode.NotFound)

                call.respond(organizacion.copy(idOrganizacion = id))
            }
            delete {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)

                println("[${call.request.origin.remoteHost}] DELETE /organizaciones/$id")

                val deletedCount = dbQuery {
                    OrganizacionesTable.update({ (OrganizacionesTable.id eq id) and (OrganizacionesTable.isDeleted eq false) }) {
                        it[isDeleted] = true
                    }
                }

                if (deletedCount == 0) return@delete call.respond(HttpStatusCode.NotFound)

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
