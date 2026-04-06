package dev.wdona.burntout.api.latest

import dev.wdona.burntout.db.DatabaseFactory.dbQuery
import dev.wdona.burntout.db.tables.EquipoMiembrosTable
import dev.wdona.burntout.db.tables.EquiposTable
import dev.wdona.burntout.db.tables.UsuariosTable
import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.domain.Usuario
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.origin
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun Route.equiposRoutes() {
    route("/equipos") {
        get {
            val idOrg = call.request.queryParameters["idOrg"]?.toLongOrNull()
            println("[${call.request.origin.remoteHost}] GET /equipos idOrg=$idOrg")
            val resultado = dbQuery {
                val query = if (idOrg != null) {
                    EquiposTable.selectAll().where { EquiposTable.idOrganizacion eq idOrg }
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
            println("[${call.request.origin.remoteHost}] POST /equipos titulo=${equipo.titulo}")
            val nuevoId = dbQuery {
                val id = EquiposTable.insert {
                    it[titulo] = equipo.titulo
                    it[puntuacion] = equipo.puntuacion
                    it[idOrganizacion] = equipo.idOrganizacion
                }[EquiposTable.id]
                
                equipo.idMiembros.forEach { idMiembroParam ->
                    val userRow = UsuariosTable.selectAll().where { UsuariosTable.id eq idMiembroParam }.singleOrNull()
                    val idEquipoAnterior = userRow?.get(UsuariosTable.idEquipo) ?: 0L

                    EquipoMiembrosTable.deleteWhere { idMiembro eq idMiembroParam }
                    
                    EquipoMiembrosTable.insert {
                        it[idEquipo] = id
                        it[idMiembro] = idMiembroParam
                    }
                    
                    UsuariosTable.update({ UsuariosTable.id eq idMiembroParam }) {
                        it[idEquipo] = id
                    }

                    if (idEquipoAnterior > 0L && idEquipoAnterior != id) {
                        val miembrosRestantes = EquipoMiembrosTable
                            .selectAll().where { EquipoMiembrosTable.idEquipo eq idEquipoAnterior }
                            .count()
                        if (miembrosRestantes == 0L) {
                            EquiposTable.deleteWhere { EquiposTable.id eq idEquipoAnterior }
                        }
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
                println("[${call.request.origin.remoteHost}] GET /equipos/$id")
                val equipo = dbQuery {
                    val row = EquiposTable.selectAll().where { EquiposTable.id eq id }.singleOrNull()
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

                        println("Miembros del equipo $id: $miembros")
                    } else null
                } ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(equipo)
            }
            put {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)
                val equipo = call.receive<Equipo>()
                println("[${call.request.origin.remoteHost}] PUT /equipos/$id titulo=${equipo.titulo}")
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
                println("[${call.request.origin.remoteHost}] DELETE /equipos/$id")
                val deletedCount = dbQuery {
                    EquipoMiembrosTable.deleteWhere { EquipoMiembrosTable.idEquipo eq id }
                    EquiposTable.deleteWhere { EquiposTable.id eq id }
                }
                if (deletedCount == 0) return@delete call.respond(HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.NoContent)
            }
            route("/miembros") {
                get {
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest)
                    println("[${call.request.origin.remoteHost}] GET /equipos/$id/miembros")
                    val miembros = dbQuery {
                        val ids = EquipoMiembrosTable
                            .selectAll().where { EquipoMiembrosTable.idEquipo eq id }
                            .map { it[EquipoMiembrosTable.idMiembro] }
                        if (ids.isEmpty()) emptyList<Usuario>()
                        else UsuariosTable.selectAll().where { UsuariosTable.id inList ids }.map {
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
                    val teamExists = dbQuery {
                        EquiposTable.selectAll().where { EquiposTable.id eq id }.count() > 0
                    }
                    if (!teamExists) return@get call.respond(HttpStatusCode.NotFound)
                    call.respond(miembros)
                }
                post("/{idUsuario}") {
                    val idEquipoNuevo = call.parameters["id"]?.toLongOrNull()
                        ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val idUsuario = call.parameters["idUsuario"]?.toLongOrNull()
                        ?: return@post call.respond(HttpStatusCode.BadRequest)

                    println("[${call.request.origin.remoteHost}] POST /equipos/$idEquipoNuevo/miembros/$idUsuario")

                    val success = dbQuery {
                        val teamExists = EquiposTable.selectAll().where { EquiposTable.id eq idEquipoNuevo }.count() > 0
                        val userRow = UsuariosTable.selectAll().where { UsuariosTable.id eq idUsuario }.singleOrNull()

                        if (teamExists && userRow != null) {
                            val idEquipoAnterior = userRow[UsuariosTable.idEquipo]

                            EquipoMiembrosTable.deleteWhere { EquipoMiembrosTable.idMiembro eq idUsuario }

                            EquipoMiembrosTable.insertIgnore {
                                it[EquipoMiembrosTable.idEquipo] = idEquipoNuevo
                                it[EquipoMiembrosTable.idMiembro] = idUsuario
                            }

                            UsuariosTable.update({ UsuariosTable.id eq idUsuario }) {
                                it[UsuariosTable.idEquipo] = idEquipoNuevo
                            }

                            if (idEquipoAnterior > 0L && idEquipoAnterior != idEquipoNuevo) {
                                val remainingMembers = EquipoMiembrosTable
                                    .selectAll().where { EquipoMiembrosTable.idEquipo eq idEquipoAnterior }
                                    .count()
                                if (remainingMembers == 0L) {
                                    EquiposTable.deleteWhere { EquiposTable.id eq idEquipoAnterior }
                                    println("Equipo $idEquipoAnterior eliminado por quedarse sin miembros.")
                                }
                            }
                            true
                        } else {
                            false
                        }
                    }

                    if (success) {
                        call.respond(HttpStatusCode.OK, "Usuario añadido al equipo y eliminado del anterior")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Equipo o Usuario no encontrado")
                    }
                }
            }
        }
    }
}
