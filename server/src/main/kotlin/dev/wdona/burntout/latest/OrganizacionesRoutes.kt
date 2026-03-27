package dev.wdona.burntout.latest

import dev.wdona.burntout.shared.domain.Organizacion
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.organizacionesRoutes() {
    route("/organizaciones") {
        get {
            call.respond(organizaciones)
        }

        post {
            val organizacion = call.receive<Organizacion>()

            val nueva = organizacion.copy(idOrganizacion = organizacionIdContador++)

            organizaciones.add(nueva)
            call.respond(HttpStatusCode.Created, nueva)
        }

        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)

                val organizacion = organizaciones.find { it.idOrganizacion == id }
                    ?: return@get call.respond(HttpStatusCode.NotFound)

                call.respond(organizacion)
            }

            put {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)

                val organizacion = call.receive<Organizacion>()

                val index = organizaciones.indexOfFirst { it.idOrganizacion == id }

                if (index == -1) return@put call.respond(HttpStatusCode.NotFound)

                organizaciones[index] = organizacion.copy(idOrganizacion = id)
                call.respond(organizaciones[index])
            }

            delete {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)

                val eliminado = organizaciones.removeIf { it.idOrganizacion == id }

                if (!eliminado) return@delete call.respond(HttpStatusCode.NotFound)

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}