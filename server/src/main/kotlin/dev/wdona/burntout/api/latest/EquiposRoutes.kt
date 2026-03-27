package dev.wdona.burntout.api.latest
import dev.wdona.burntout.db.DatabaseFactory.dbQuery
import dev.wdona.burntout.db.tables.EquipoMiembrosTable
import dev.wdona.burntout.db.tables.EquiposTable
import dev.wdona.burntout.db.tables.UsuariosTable
import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.domain.Usuario
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
fun Route.equiposRoutes() {
    route("/equipos") {
        get {
            val idOrg = call.request.queryParameters["idOrg"]?.toLongOrNull()
            val resultado = dbQuery {
                val query = if (idOrg != null) {
                    EquiposTable .selectAll().where { EquiposTable.idOrganizacion eq idOrg }
                } else {
                    EquiposTable.selectAll()
                }
                query.map { row ->
                    val eqId = row[EquiposTable.id]
                    val miembros = EquipoMiembrosTable
                         .selectAll().where { EquipoMiembrosTable.idEquipo eq eqId }
                        .map { it[EquipoMiembrosTable.idMiembro] }
                    Equipo(
                        idEquipo = eqId,
                        titulo = row[EquiposTable.titulo],
                        puntuacion = row[EquiposTable.puntuacion],
                        idOrganizacion = row[EquiposTable.idOrganizacion],
                        idMiembros = miembros
                    )
                }
            }
            call.respond(resultado)
        }
        post {
            val equipo = call.receive<Equipo>()
            val nuevoId = dbQuery {
                val id = EquiposTable.insert {
                    it[titulo] = equipo.titulo
                    it[puntuacion] = equipo.puntuacion
                    it[idOrganizacion] = equipo.idOrganizacion
                }[EquiposTable.id]
                equipo.idMiembros.forEach { mId ->
                    EquipoMiembrosTable.insert {
                        it[idEquipo] = id
                        it[idMiembro] = mId
                    }
                }
                id
            }
            call.respond(HttpStatusCode.Created, equipo.copy(idEquipo = nuevoId))
        }
        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                val equipo = dbQuery {
                    val row = EquiposTable .selectAll().where { EquiposTable.id eq id }.singleOrNull()
                    if (row != null) {
                        val miembros = EquipoMiembrosTable
                             .selectAll().where { EquipoMiembrosTable.idEquipo eq id }
                            .map { it[EquipoMiembrosTable.idMiembro] }
                        Equipo(
                            idEquipo = row[EquiposTable.id],
                            titulo = row[EquiposTable.titulo],
                            puntuacion = row[EquiposTable.puntuacion],
                            idOrganizacion = row[EquiposTable.idOrganizacion],
                            idMiembros = miembros
                        )
                    } else null
                } ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(equipo)
            }
            put {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)
                val equipo = call.receive<Equipo>()
                val isSuccess = dbQuery {
                    val updatedCount = EquiposTable.update({ EquiposTable.id eq id }) {
                        it[titulo] = equipo.titulo
                        it[puntuacion] = equipo.puntuacion
                        it[idOrganizacion] = equipo.idOrganizacion
                    }
                    if (updatedCount > 0) {
                        EquipoMiembrosTable.deleteWhere { EquipoMiembrosTable.idEquipo eq id }
                        equipo.idMiembros.forEach { mId ->
                            EquipoMiembrosTable.insert {
                                it[idEquipo] = id
                                it[idMiembro] = mId
                            }
                        }
                        true
                    } else {
                        false
                    }
                }
                if (!isSuccess) return@put call.respond(HttpStatusCode.NotFound)
                call.respond(equipo.copy(idEquipo = id))
            }
            delete {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)
                val deletedCount = dbQuery {
                    EquipoMiembrosTable.deleteWhere { EquipoMiembrosTable.idEquipo eq id }
                    EquiposTable.deleteWhere { EquiposTable.id eq id }
                }
                if (deletedCount == 0) return@delete call.respond(HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.NoContent)
            }
            get("/miembros") {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                val miembros = dbQuery {
                    val ids = EquipoMiembrosTable
                         .selectAll().where { EquipoMiembrosTable.idEquipo eq id }
                        .map { it[EquipoMiembrosTable.idMiembro] }
                    if (ids.isEmpty()) emptyList<Usuario>()
                    else UsuariosTable .selectAll().where { UsuariosTable.id inList ids }.map {
                        Usuario(
                            idUsuario = it[UsuariosTable.id],
                            username = it[UsuariosTable.username],
                            password = it[UsuariosTable.password],
                            nombre = it[UsuariosTable.nombre],
                            riesgoBurnout = it[UsuariosTable.riesgoBurnout],
                            descripcion = it[UsuariosTable.descripcion],
                            idOrganizacion = it[UsuariosTable.idOrganizacion],
                            idEquipo = it[UsuariosTable.idEquipo]
                        )
                    }
                }
                // If the team doesn't exist, we should theoretically return 404, but to keep it simple:
                // Actually let's check if the team exists
                val teamExists = dbQuery {
                    EquiposTable .selectAll().where { EquiposTable.id eq id }.count() > 0
                }
                if (!teamExists) return@get call.respond(HttpStatusCode.NotFound)
                call.respond(miembros)
            }
        }
    }
}
