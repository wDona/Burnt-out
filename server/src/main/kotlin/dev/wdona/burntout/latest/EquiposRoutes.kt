package dev.wdona.burntout.latest

import dev.wdona.burntout.shared.domain.Equipo
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.equiposRoutes() {
    route("/equipos") {
        get {
            val idOrg = call.request.queryParameters["idOrg"]?.toLongOrNull()

            val resultado = if (idOrg != null) equipos.filter { it.idOrganizacion == idOrg } else equipos

            call.respond(resultado)
        }

        post {
            val equipo = call.receive<Equipo>()

            val nuevo = equipo.copy(idEquipo = equipoIdContador++)

            equipos.add(nuevo)
            call.respond(HttpStatusCode.Created, nuevo)
        }

        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)

                val equipo = equipos.find { it.idEquipo == id }
                    ?: return@get call.respond(HttpStatusCode.NotFound)

                call.respond(equipo)
            }

            put {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)

                val equipo = call.receive<Equipo>()

                val index = equipos.indexOfFirst { it.idEquipo == id }

                if (index == -1) return@put call.respond(HttpStatusCode.NotFound)

                equipos[index] = equipo.copy(idEquipo = id)
                call.respond(equipos[index])
            }

            delete {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)

                val eliminado = equipos.removeIf { it.idEquipo == id }

                if (!eliminado) return@delete call.respond(HttpStatusCode.NotFound)

                call.respond(HttpStatusCode.NoContent)
            }

            get("/miembros") {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)

                val equipo = equipos.find { it.idEquipo == id }
                    ?: return@get call.respond(HttpStatusCode.NotFound)

                val miembros = usuarios.filter { it.idUsuario in equipo.idMiembros }

                call.respond(miembros)
            }
        }
    }
}