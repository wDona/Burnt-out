package dev.wdona.burntout.api.latest
import dev.wdona.burntout.db.DatabaseFactory.dbQuery
import dev.wdona.burntout.db.tables.UsuariosTable
import dev.wdona.burntout.db.tables.EquiposTable
import dev.wdona.burntout.db.tables.EquipoMiembrosTable
import dev.wdona.burntout.db.tables.RespuestasTable
import dev.wdona.burntout.db.tables.OrganizacionesTable
import dev.wdona.burntout.db.tables.InvitacionesTable
import dev.wdona.burntout.db.tables.PreguntasTable
import dev.wdona.burntout.shared.domain.Usuario
import dev.wdona.burntout.shared.domain.RegistroRequest
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
internal data class LoginRequest(val username: String, val contrasena: String)

private fun rowToUsuario(it: ResultRow) = Usuario(
    idUsuario = it[UsuariosTable.id],
    username = it[UsuariosTable.username],
    password = it[UsuariosTable.password],
    nombre = it[UsuariosTable.nombre],
    riesgoBurnout = it[UsuariosTable.riesgoBurnout],
    descripcion = it[UsuariosTable.descripcion],
    idOrganizacion = it[UsuariosTable.idOrganizacion],
    idEquipo = it[UsuariosTable.idEquipo],
    rol = it[UsuariosTable.rol]
)
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
                query.map { rowToUsuario(it) }
            }
            call.respond(resultado)
        }
        post {
            val request = call.receive<RegistroRequest>()
            println("[${call.request.origin.remoteHost}] POST /usuarios username=${request.username} modo=${request.modo}")

            val existe = dbQuery {
                UsuariosTable.selectAll().where { UsuariosTable.username eq request.username }.count() > 0
            }
            if (existe) {
                return@post call.respond(HttpStatusCode.Conflict, "El nombre de usuario ya existe")
            }

            when (request.modo) {
                "CREAR_ORG" -> {
                    val nombreOrg = request.nombreOrg?.takeIf { it.isNotBlank() } ?: "Org de ${request.nombre}"
                    val usuario = crearUsuarioConOrg(request, nombreOrg)
                    call.respond(HttpStatusCode.Created, usuario)
                }

                "UNIRSE" -> {
                    val codigo = request.codigoInvitacion
                        ?: return@post call.respond(HttpStatusCode.BadRequest, "Falta codigoInvitacion")
                    try {
                        val usuario = unirseConCodigo(request, codigo)
                        call.respond(HttpStatusCode.Created, usuario)
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, e.message ?: "Código inválido")
                    }
                }

                else -> call.respond(HttpStatusCode.BadRequest, "modo inválido: usa CREAR_ORG o UNIRSE")
            }
        }
        post("/login") {
            println("[${call.request.origin.remoteHost}] POST /usuarios/login")
            val request = try {
                call.receive<LoginRequest>()
            } catch (e: Exception) {
                println("[LOGIN ERROR] ${e::class.simpleName}: ${e.message}")
                println("[LOGIN ERROR CAUSE] ${e.cause?.message}")
                return@post call.respond(HttpStatusCode.BadRequest)
            }
            println("[${call.request.origin.remoteHost}] POST /usuarios/login username=${request.username} password=${request.contrasena}")
            val usuario = dbQuery {
                UsuariosTable.selectAll().where {
                    (UsuariosTable.username eq request.username) and (UsuariosTable.password eq request.contrasena)
                }.map { rowToUsuario(it) }.singleOrNull()
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
                    UsuariosTable.selectAll().where { UsuariosTable.id eq id }
                        .map { rowToUsuario(it) }.singleOrNull()
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
        get("/username/{username}") {
            val username = call.parameters["username"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            println("[${call.request.origin.remoteHost}] GET /usuarios/username/$username")
            val usuario = dbQuery {
                UsuariosTable.selectAll().where { UsuariosTable.username eq username }
                    .map { rowToUsuario(it) }.singleOrNull()
            } ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(usuario)
        }
    }
}

private suspend fun crearUsuarioConOrg(request: RegistroRequest, nombreOrg: String): Usuario = dbQuery {
    val idOrg = OrganizacionesTable.insert {
        it[OrganizacionesTable.nombre] = nombreOrg
    }[OrganizacionesTable.id]

    val idEquipo = EquiposTable.insert {
        it[EquiposTable.titulo] = "Equipo de ${request.nombre}"
        it[EquiposTable.puntuacion] = 0L
        it[EquiposTable.idOrganizacion] = idOrg
    }[EquiposTable.id]

    val idUsuario = UsuariosTable.insert {
        it[UsuariosTable.username] = request.username
        it[UsuariosTable.password] = request.password
        it[UsuariosTable.nombre] = request.nombre
        it[UsuariosTable.riesgoBurnout] = 0.0
        it[UsuariosTable.descripcion] = ""
        it[UsuariosTable.idOrganizacion] = idOrg
        it[UsuariosTable.idEquipo] = idEquipo
        it[UsuariosTable.rol] = "ADMIN"
    }[UsuariosTable.id]

    EquipoMiembrosTable.insert {
        it[EquipoMiembrosTable.idEquipo] = idEquipo
        it[EquipoMiembrosTable.idMiembro] = idUsuario
    }

    Usuario(
        idUsuario = idUsuario,
        username = request.username,
        password = request.password,
        nombre = request.nombre,
        riesgoBurnout = 0.0,
        descripcion = "",
        idOrganizacion = idOrg,
        idEquipo = idEquipo,
        rol = "ADMIN"
    )
}

private suspend fun unirseConCodigo(request: RegistroRequest, codigo: String): Usuario {
    val ahora = System.currentTimeMillis()

    val invRow = dbQuery {
        InvitacionesTable.selectAll().where { InvitacionesTable.code eq codigo }.singleOrNull()
    } ?: throw IllegalArgumentException("Código inválido")

    if (invRow[InvitacionesTable.usadoEn] != null) throw IllegalArgumentException("Código ya utilizado")
    val expira = invRow[InvitacionesTable.expiraEn]
    if (expira != null && expira < ahora) throw IllegalArgumentException("Código expirado")

    val idOrg = invRow[InvitacionesTable.idOrganizacion]
    val rolCodigo = invRow[InvitacionesTable.rol]

    return dbQuery {
        val idEquipo = EquiposTable.insert {
            it[EquiposTable.titulo] = "Equipo de ${request.nombre}"
            it[EquiposTable.puntuacion] = 0L
            it[EquiposTable.idOrganizacion] = idOrg
        }[EquiposTable.id]

        val idUsuario = UsuariosTable.insert {
            it[UsuariosTable.username] = request.username
            it[UsuariosTable.password] = request.password
            it[UsuariosTable.nombre] = request.nombre
            it[UsuariosTable.riesgoBurnout] = 0.0
            it[UsuariosTable.descripcion] = ""
            it[UsuariosTable.idOrganizacion] = idOrg
            it[UsuariosTable.idEquipo] = idEquipo
            it[UsuariosTable.rol] = rolCodigo
        }[UsuariosTable.id]

        EquipoMiembrosTable.insert {
            it[EquipoMiembrosTable.idEquipo] = idEquipo
            it[EquipoMiembrosTable.idMiembro] = idUsuario
        }

        InvitacionesTable.update({ InvitacionesTable.code eq codigo }) {
            it[InvitacionesTable.usadoEn] = ahora
            it[InvitacionesTable.usadoPor] = idUsuario
        }

        val preguntas = PreguntasTable.selectAll()
            .where { PreguntasTable.idOrganizacion eq idOrg }
            .map { it[PreguntasTable.id] }

        preguntas.forEach { preguntaId ->
            RespuestasTable.insert {
                it[RespuestasTable.idPregunta] = preguntaId
                it[RespuestasTable.idUsuario] = idUsuario
                it[RespuestasTable.respuesta] = -1
                it[RespuestasTable.anonimo] = false
            }
        }

        Usuario(
            idUsuario = idUsuario,
            username = request.username,
            password = request.password,
            nombre = request.nombre,
            riesgoBurnout = 0.0,
            descripcion = "",
            idOrganizacion = idOrg,
            idEquipo = idEquipo,
            rol = rolCodigo
        )
    }
}
