package dev.wdona.burntout.latest

import dev.wdona.burntout.shared.domain.Usuario
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

@Serializable
private data class LoginRequest(val username: String, val contrasena: String)

fun Route.usuariosRoutes() {
    route("/usuarios") {
        get {
            val idOrg = call.request.queryParameters["idOrg"]?.toLongOrNull()
            val idEquipo = call.request.queryParameters["idEquipo"]?.toLongOrNull()

            val resultado = usuarios
                .let { if (idOrg != null) it.filter { u -> u.idOrganizacion == idOrg } else it }
                .let { if (idEquipo != null) it.filter { u -> u.idEquipo == idEquipo } else it }

            call.respond(resultado)
        }

        post {
            val usuario = call.receive<Usuario>()
            val nuevo = usuario.copy(idUsuario = usuarioIdContador++)

            usuarios.add(nuevo)
            call.respond(HttpStatusCode.Created, nuevo)
        }

        post("/login") {
            val request = call.receive<LoginRequest>()

            val usuario = usuarios.find { it.username == request.username && it.password == request.contrasena }
                ?: return@post call.respond(HttpStatusCode.Unauthorized)

            call.respond(usuario)
        }

        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)

                val usuario = usuarios.find { it.idUsuario == id }
                    ?: return@get call.respond(HttpStatusCode.NotFound)

                call.respond(usuario)
            }

            put {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)

                val usuario = call.receive<Usuario>()
                val index = usuarios.indexOfFirst { it.idUsuario == id }

                if (index == -1) return@put call.respond(HttpStatusCode.NotFound)

                usuarios[index] = usuario.copy(idUsuario = id)
                call.respond(usuarios[index])
            }

            delete {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)

                val eliminado = usuarios.removeIf { it.idUsuario == id }

                if (!eliminado) return@delete call.respond(HttpStatusCode.NotFound)

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}