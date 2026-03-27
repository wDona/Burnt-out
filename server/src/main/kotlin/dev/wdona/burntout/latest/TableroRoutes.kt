package dev.wdona.burntout.latest

import dev.wdona.burntout.shared.domain.Tablero
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.tablerosRoutes() {
    route("/tableros") {
        get {
            val idOrg = call.request.queryParameters["idOrg"]?.toLongOrNull()
            val idEquipo = call.request.queryParameters["idEquipo"]?.toLongOrNull()

            val resultado = tableros
                .let { if (idOrg != null) it.filter { t -> t.idOrganizacion == idOrg } else it }
                .let { if (idEquipo != null) it.filter { t -> t.idEquipo == idEquipo } else it }

            call.respond(resultado)
        }

        post {
            val tablero = call.receive<Tablero>()
            val nuevo = tablero.copy(idTablero = tableroIdContador++)

            tableros.add(nuevo)
            call.respond(HttpStatusCode.Created, nuevo)
        }

        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)

                val tablero = tableros.find { it.idTablero == id }
                    ?: return@get call.respond(HttpStatusCode.NotFound)

                call.respond(tablero)
            }

            put {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)

                val tablero = call.receive<Tablero>()

                val index = tableros.indexOfFirst { it.idTablero == id }

                if (index == -1) return@put call.respond(HttpStatusCode.NotFound)

                tableros[index] = tablero.copy(idTablero = id)
                call.respond(tableros[index])
            }

            delete {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)

                val eliminado = tableros.removeIf { it.idTablero == id }

                if (!eliminado) return@delete call.respond(HttpStatusCode.NotFound)

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}