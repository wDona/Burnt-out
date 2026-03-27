package dev.wdona.burntout.latest

import dev.wdona.burntout.domain.model.Ajuste
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.ajustesRoutes() {
    route("/ajustes/{idUsuario}") {
        get {
            val idUsuario = call.parameters["idUsuario"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            call.respond(ajustesPorUsuario.getOrDefault(idUsuario, emptyList<Ajuste>()))
        }

        post {
            val idUsuario = call.parameters["idUsuario"]?.toLongOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest)

            val ajuste = call.receive<Ajuste>()

            val nuevo = Ajuste(ajusteIdContador++, ajuste.nombre, ajuste.valorAjuste)

            ajustesPorUsuario.getOrPut(idUsuario) { mutableListOf() }.add(nuevo)
            call.respond(HttpStatusCode.Created, nuevo)
        }

        route("/{id}") {
            put {
                val idUsuario = call.parameters["idUsuario"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)

                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)

                val ajuste = call.receive<Ajuste>()

                val lista = ajustesPorUsuario[idUsuario]
                    ?: return@put call.respond(HttpStatusCode.NotFound)

                val index = lista.indexOfFirst { it.idAjuste == id }

                if (index == -1) return@put call.respond(HttpStatusCode.NotFound)

                lista[index] = Ajuste(id, ajuste.nombre, ajuste.valorAjuste)
                call.respond(lista[index])
            }

            delete {
                val idUsuario = call.parameters["idUsuario"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)

                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)

                val lista = ajustesPorUsuario[idUsuario]
                    ?: return@delete call.respond(HttpStatusCode.NotFound)

                val eliminado = lista.removeIf { it.idAjuste == id }

                if (!eliminado) return@delete call.respond(HttpStatusCode.NotFound)

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}