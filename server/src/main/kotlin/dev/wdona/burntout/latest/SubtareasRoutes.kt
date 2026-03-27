package dev.wdona.burntout.latest

import dev.wdona.burntout.shared.domain.Subtarea
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

@Serializable
private data class CompletadoRequest(val completado: Boolean)

fun Route.subtareasRoutes() {
    route("/subtareas") {
        get {
            val idTarea = call.request.queryParameters["idTarea"]?.toLongOrNull()

            val resultado = if (idTarea != null) subtareas.filter { it.idTareaPerteneciente == idTarea } else subtareas

            call.respond(resultado)
        }

        post {
            val subtarea = call.receive<Subtarea>()
            val nueva = subtarea.copy(idSubtarea = subtareaIdContador++)

            subtareas.add(nueva)
            call.respond(HttpStatusCode.Created, nueva)
        }

        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)

                val subtarea = subtareas.find { it.idSubtarea == id }
                    ?: return@get call.respond(HttpStatusCode.NotFound)

                call.respond(subtarea)
            }

            patch {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest)

                val request = call.receive<CompletadoRequest>()

                val index = subtareas.indexOfFirst { it.idSubtarea == id }

                if (index == -1) return@patch call.respond(HttpStatusCode.NotFound)

                subtareas[index] = subtareas[index].copy(completado = request.completado)
                call.respond(subtareas[index])
            }

            delete {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)

                val eliminado = subtareas.removeIf { it.idSubtarea == id }

                if (!eliminado) return@delete call.respond(HttpStatusCode.NotFound)

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}