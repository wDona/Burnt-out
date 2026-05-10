package dev.wdona.burntout.api.latest
import dev.wdona.burntout.db.DatabaseFactory
import dev.wdona.burntout.db.DatabaseFactory.dbQuery
import dev.wdona.burntout.db.tables.UsuariosTable
import dev.wdona.burntout.db.tables.EquiposTable
import dev.wdona.burntout.db.tables.EquipoMiembrosTable
import dev.wdona.burntout.db.tables.RespuestasTable
import dev.wdona.burntout.db.tables.OrganizacionesTable
import dev.wdona.burntout.db.tables.InvitacionesTable
import dev.wdona.burntout.db.tables.PreguntasTable
import dev.wdona.burntout.db.tables.SesionesTable
import dev.wdona.burntout.shared.domain.LoginResponse
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
import org.mindrot.jbcrypt.BCrypt

private fun hashPassword(plain: String): String = BCrypt.hashpw(plain, BCrypt.gensalt())
private fun String.isBcryptHash() = startsWith("\$2a\$") || startsWith("\$2b\$")
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
    rol = it[UsuariosTable.rol],
    isDeleted = it[UsuariosTable.isDeleted]
)
fun Route.usuariosRoutes() {
    route("/usuarios") {
        get {
            val idOrg = call.request.queryParameters["idOrg"]?.toLongOrNull()
            val idEquipo = call.request.queryParameters["idEquipo"]?.toLongOrNull()
            println("[${call.request.origin.remoteHost}] GET /usuarios idOrg=$idOrg idEquipo=$idEquipo")
            val resultado = dbQuery {
                val query = UsuariosTable.selectAll().where { UsuariosTable.isDeleted eq false }
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
                UsuariosTable.selectAll().where { (UsuariosTable.username eq request.username) and (UsuariosTable.isDeleted eq false) }.count() > 0
            }
            if (existe) {
                return@post call.respond(HttpStatusCode.Conflict, "El nombre de usuario ya existe")
            }

            when (request.modo) {
                "CREAR_ORG" -> {
                    val nombreOrg = request.nombreOrg?.takeIf { it.isNotBlank() } ?: "Org de ${request.nombre}"
                    val usuario = crearUsuarioConOrg(request, nombreOrg)
                    val token = crearSesion(usuario.idUsuario)
                    call.respond(HttpStatusCode.Created, LoginResponse(usuario, token))
                }

                "UNIRSE" -> {
                    val codigo = request.codigoInvitacion
                        ?: return@post call.respond(HttpStatusCode.BadRequest, "Falta codigoInvitacion")
                    try {
                        val usuario = unirseConCodigo(request, codigo)
                        val token = crearSesion(usuario.idUsuario)
                        call.respond(HttpStatusCode.Created, LoginResponse(usuario, token))
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
            println("[${call.request.origin.remoteHost}] POST /usuarios/login username=${request.username}")
            val usuario = dbQuery {
                UsuariosTable.selectAll().where {
                    (UsuariosTable.username eq request.username) and (UsuariosTable.isDeleted eq false)
                }.map { rowToUsuario(it) }.singleOrNull()
            } ?: return@post call.respond(HttpStatusCode.Unauthorized)

            val storedPassword = usuario.password
            val passwordMatch = if (storedPassword.isBcryptHash()) {
                BCrypt.checkpw(request.contrasena, storedPassword)
            } else {
                false
            }
            if (!passwordMatch) return@post call.respond(HttpStatusCode.Unauthorized)

            val token = crearSesion(usuario.idUsuario)
            call.respond(LoginResponse(usuario, token))
        }
        get("/existe/{username}") {
            val username = call.parameters["username"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            println("[${call.request.origin.remoteHost}] GET /usuarios/existe/$username")
            val existe = dbQuery {
                UsuariosTable.selectAll().where { (UsuariosTable.username eq username) and (UsuariosTable.isDeleted eq false) }.count() > 0
            }
            call.respond(existe)
        }
        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                println("[${call.request.origin.remoteHost}] GET /usuarios/$id")
                val usuario = dbQuery {
                    UsuariosTable.selectAll().where { (UsuariosTable.id eq id) and (UsuariosTable.isDeleted eq false) }
                        .map { rowToUsuario(it) }.singleOrNull()
                } ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(usuario)
            }
            put {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)
                val usuario = call.receive<Usuario>()
                println("[${call.request.origin.remoteHost}] PUT /usuarios/$id username=${usuario.username}")
                val passwordToStore = if (usuario.password.isBcryptHash()) usuario.password else hashPassword(usuario.password)
                val updatedCount = dbQuery {
                    UsuariosTable.update({ UsuariosTable.id eq id }) {
                        it[username] = usuario.username
                        it[password] = passwordToStore
                        it[nombre] = usuario.nombre
                        it[riesgoBurnout] = usuario.riesgoBurnout
                        it[descripcion] = usuario.descripcion
                        it[idOrganizacion] = usuario.idOrganizacion
                        it[idEquipo] = usuario.idEquipo
                        it[isDeleted] = usuario.isDeleted
                        it[updatedAt] = System.currentTimeMillis() / 1000
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
                    UsuariosTable.update({ (UsuariosTable.id eq id) and (UsuariosTable.isDeleted eq false) }) {
                        it[isDeleted] = true
                    }
                }
                if (deletedCount == 0) return@delete call.respond(HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.NoContent)
            }
        }
        put("/{id}/rol/{nuevoRol}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest)
            val nuevoRol = call.parameters["nuevoRol"]
                ?: return@put call.respond(HttpStatusCode.BadRequest)
            val idAdmin = call.request.queryParameters["idAdmin"]?.toLongOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest)

            if (nuevoRol !in listOf("MEMBER", "ADMIN")) {
                return@put call.respond(HttpStatusCode.BadRequest, "Rol no válido")
            }

            println("[${call.request.origin.remoteHost}] PUT /usuarios/$id/rol/$nuevoRol idAdmin=$idAdmin")

            val result = dbQuery {
                val adminRow = UsuariosTable.selectAll()
                    .where { (UsuariosTable.id eq idAdmin) and (UsuariosTable.isDeleted eq false) }
                    .singleOrNull()
                    ?: return@dbQuery HttpStatusCode.Forbidden to "Admin no encontrado"

                val adminRol = adminRow[UsuariosTable.rol]
                if (adminRol != "ADMIN" && adminRol != "OWNER") {
                    return@dbQuery HttpStatusCode.Forbidden to "Sin permisos para cambiar roles"
                }

                val targetRow = UsuariosTable.selectAll()
                    .where { (UsuariosTable.id eq id) and (UsuariosTable.isDeleted eq false) }
                    .singleOrNull()
                    ?: return@dbQuery HttpStatusCode.NotFound to "Usuario no encontrado"

                if (targetRow[UsuariosTable.rol] == "OWNER") {
                    return@dbQuery HttpStatusCode.Forbidden to "No se puede cambiar el rol de un OWNER"
                }

                UsuariosTable.update({ UsuariosTable.id eq id }) {
                    it[rol] = nuevoRol
                }
                HttpStatusCode.OK to "Rol actualizado"
            }
            call.respond(result.first, result.second)
        }
        get("/username/{username}") {
            val username = call.parameters["username"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            println("[${call.request.origin.remoteHost}] GET /usuarios/username/$username")
            val usuario = dbQuery {
                UsuariosTable.selectAll().where { (UsuariosTable.username eq username) and (UsuariosTable.isDeleted eq false) }
                    .map { rowToUsuario(it) }.singleOrNull()
            } ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(usuario)
        }
    }
}

private suspend fun crearSesion(idUsuario: Long): String = dbQuery {
    val token = java.util.UUID.randomUUID().toString()
    SesionesTable.insert {
        it[SesionesTable.token] = token
        it[SesionesTable.idUsuario] = idUsuario
        it[SesionesTable.creadoEn] = System.currentTimeMillis() / 1000
    }
    token
}

private suspend fun crearUsuarioConOrg(request: RegistroRequest, nombreOrg: String): Usuario = dbQuery {
    println("[SERVER] Creando organización: $nombreOrg")
    val idOrg = OrganizacionesTable.insert {
        it[OrganizacionesTable.nombre] = nombreOrg
    }[OrganizacionesTable.id]
    println("[SERVER] Organización creada con ID: $idOrg")

    DatabaseFactory.insertPreguntasMBI(idOrg)

    println("[SERVER] Creando equipo para el usuario: ${request.nombre}")
    val idEquipo = EquiposTable.insert {
        it[EquiposTable.titulo] = "Equipo de ${request.nombre}"
        it[EquiposTable.puntuacion] = 0L
        it[EquiposTable.idOrganizacion] = idOrg
    }[EquiposTable.id]
    println("[SERVER] Equipo creado con ID: $idEquipo")

    println("[SERVER] Creando usuario OWNER: ${request.username}")
    val hashedPassword = hashPassword(request.password)
    val idUsuario = UsuariosTable.insert {
        it[UsuariosTable.username] = request.username
        it[UsuariosTable.password] = hashedPassword
        it[UsuariosTable.nombre] = request.nombre
        it[UsuariosTable.riesgoBurnout] = -1.0
        it[UsuariosTable.descripcion] = ""
        it[UsuariosTable.idOrganizacion] = idOrg
        it[UsuariosTable.idEquipo] = idEquipo
        it[UsuariosTable.rol] = "OWNER"
    }[UsuariosTable.id]
    println("[SERVER] Usuario creado con ID: $idUsuario")

    EquipoMiembrosTable.insert {
        it[EquipoMiembrosTable.idEquipo] = idEquipo
        it[EquipoMiembrosTable.idMiembro] = idUsuario
    }

    println("[SERVER] Inicializando respuestas placeholder para el usuario $idUsuario en org $idOrg")
    val preguntas = PreguntasTable.selectAll()
        .where { (PreguntasTable.idOrganizacion eq idOrg) and (PreguntasTable.isDeleted eq false) }
        .map { it[PreguntasTable.id] }

    println("[SERVER] Se encontraron ${preguntas.size} preguntas para inicializar respuestas")
    preguntas.forEach { preguntaId ->
        RespuestasTable.insert {
            it[RespuestasTable.id] = java.util.UUID.randomUUID().toString()
            it[RespuestasTable.idPregunta] = preguntaId
            it[RespuestasTable.idUsuario] = idUsuario
            it[RespuestasTable.respuesta] = -1
            it[RespuestasTable.anonimo] = false
        }
    }
    println("[SERVER] Respuestas placeholder insertadas")

    Usuario(
        idUsuario = idUsuario,
        username = request.username,
        password = hashedPassword,
        nombre = request.nombre,
        riesgoBurnout = -1.0,
        descripcion = "",
        idOrganizacion = idOrg,
        idEquipo = idEquipo,
        rol = "OWNER"
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

    val hashedPassword = hashPassword(request.password)

    return dbQuery {
        val idEquipo = EquiposTable.insert {
            it[EquiposTable.titulo] = "Equipo de ${request.nombre}"
            it[EquiposTable.puntuacion] = 0L
            it[EquiposTable.idOrganizacion] = idOrg
        }[EquiposTable.id]

        val idUsuario = UsuariosTable.insert {
            it[UsuariosTable.username] = request.username
            it[UsuariosTable.password] = hashedPassword
            it[UsuariosTable.nombre] = request.nombre
            it[UsuariosTable.riesgoBurnout] = -1.0
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
            .where { (PreguntasTable.idOrganizacion eq idOrg) and (PreguntasTable.isDeleted eq false) }
            .map { it[PreguntasTable.id] }

        preguntas.forEach { preguntaId ->
            RespuestasTable.insert {
                it[RespuestasTable.id] = java.util.UUID.randomUUID().toString()
                it[RespuestasTable.idPregunta] = preguntaId
                it[RespuestasTable.idUsuario] = idUsuario
                it[RespuestasTable.respuesta] = -1
                it[RespuestasTable.anonimo] = false
            }
        }

        Usuario(
            idUsuario = idUsuario,
            username = request.username,
            password = hashedPassword,
            nombre = request.nombre,
            riesgoBurnout = -1.0,
            descripcion = "",
            idOrganizacion = idOrg,
            idEquipo = idEquipo,
            rol = rolCodigo
        )
    }
}
