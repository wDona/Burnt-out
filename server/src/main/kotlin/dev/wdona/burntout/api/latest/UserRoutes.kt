package dev.wdona.burntout.api.latest
import dev.wdona.burntout.db.DatabaseFactory.dbQuery
import dev.wdona.burntout.db.tables.UsuariosTable
import dev.wdona.burntout.db.tables.EquiposTable
import dev.wdona.burntout.db.tables.EquipoMiembrosTable
import dev.wdona.burntout.shared.domain.Usuario
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.origin
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
@Serializable
private data class LoginRequest(val username: String, val contrasena: String)
fun Route.usuariosRoutes() {
    route("/usuarios") {
        get {
            val idOrg = call.request.queryParameters["idOrg"]?.toLongOrNull()
            val idEquipo = call.request.queryParameters["idEquipo"]?.toLongOrNull()
            println("[${call.request.origin.remoteHost}] GET /usuarios idOrg=$idOrg idEquipo=$idEquipo")
            val resultado = dbQuery {
                val query = UsuariosTable.selectAll()
                if (idOrg != null) {
                    query.andWhere { UsuariosTable.idOrganizacion eq idOrg }
                }
                if (idEquipo != null) {
                    query.andWhere { UsuariosTable.idEquipo eq idEquipo }
                }
                query.map {
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
            call.respond(resultado)
        }
        post {
            val usuario = call.receive<Usuario>()
            println("[${call.request.origin.remoteHost}] POST /usuarios username=${usuario.username}")
            var createdEquipoId = usuario.idEquipo
            val nuevoId = dbQuery {
                var equipoId = usuario.idEquipo
                if (equipoId <= 0L) {
                    equipoId = EquiposTable.insert {
                        it[titulo] = "Equipo de ${usuario.nombre}"
                        it[puntuacion] = 0L
                        it[idOrganizacion] = usuario.idOrganizacion
                    }[EquiposTable.id]
                }
                createdEquipoId = equipoId
                
                val nuevoUserId = UsuariosTable.insert {
                    it[username] = usuario.username
                    it[password] = usuario.password
                    it[nombre] = usuario.nombre
                    it[riesgoBurnout] = usuario.riesgoBurnout
                    it[descripcion] = usuario.descripcion
                    it[idOrganizacion] = usuario.idOrganizacion
                    it[idEquipo] = equipoId
                }[UsuariosTable.id]
                
                EquipoMiembrosTable.insert {
                    it[this.idEquipo] = equipoId
                    it[this.idMiembro] = nuevoUserId
                }
                
                nuevoUserId
            }
            call.respond(HttpStatusCode.Created, usuario.copy(idUsuario = nuevoId, idEquipo = createdEquipoId))
        }
        post("/login") {
            println("[${call.request.origin.remoteHost}] POST /usuarios/login - ContentType: ${call.request.headers["Content-Type"]}")
            val request = try {
                call.receive<LoginRequest>()
            } catch (e: Exception) {
                println("[LOGIN ERROR] ${e::class.simpleName}: ${e.message}")
                println("[LOGIN ERROR CAUSE] ${e.cause?.message}")
                return@post call.respond(HttpStatusCode.BadRequest)
            }
            println("[${call.request.origin.remoteHost}] POST /usuarios/login username=${request.username}")
            val usuario = dbQuery {
                UsuariosTable .selectAll().where {
                    (UsuariosTable.username eq request.username) and (UsuariosTable.password eq request.contrasena)
                }.map {
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
                }.singleOrNull()
            } ?: return@post call.respond(HttpStatusCode.Unauthorized)
            call.respond(usuario)
        }
        get("/existe/{username}") {
            val username = call.parameters["username"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            println("[${call.request.origin.remoteHost}] GET /usuarios/existe/$username")
            val existe = dbQuery {
                UsuariosTable.selectAll().where { UsuariosTable.username eq username }.count() > 0
            }
            call.respond(existe)
        }
        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                println("[${call.request.origin.remoteHost}] GET /usuarios/$id")
                val usuario = dbQuery {
                    UsuariosTable .selectAll().where { UsuariosTable.id eq id }.map {
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
                    }.singleOrNull()
                } ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(usuario)
            }
            put {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)
                val usuario = call.receive<Usuario>()
                println("[${call.request.origin.remoteHost}] PUT /usuarios/$id username=${usuario.username}")
                val updatedCount = dbQuery {
                    UsuariosTable.update({ UsuariosTable.id eq id }) {
                        it[username] = usuario.username
                        it[password] = usuario.password
                        it[nombre] = usuario.nombre
                        it[riesgoBurnout] = usuario.riesgoBurnout
                        it[descripcion] = usuario.descripcion
                        it[idOrganizacion] = usuario.idOrganizacion
                        it[idEquipo] = usuario.idEquipo
                    }
                }
                if (updatedCount == 0) return@put call.respond(HttpStatusCode.NotFound)
                call.respond(usuario.copy(idUsuario = id))
            }
            delete {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)
                println("[${call.request.origin.remoteHost}] DELETE /usuarios/$id")
                val deletedCount = dbQuery {
                    UsuariosTable.deleteWhere { UsuariosTable.id eq id }
                }
                if (deletedCount == 0) return@delete call.respond(HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
