package dev.wdona.burntout.latest

import dev.wdona.burntout.shared.domain.Tarea
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

@Serializable
private data class EstadoRequest(val estado: String)

fun Route.tareasRoutes() {
    route("/tareas") {
        get {
            val idTablero = call.request.queryParameters["idTablero"]?.toLongOrNull()

            val resultado = if (idTablero != null) tareas.filter { it.idTableroPerteneciente == idTablero } else tareas
            call.respond(resultado)
        }

        post {
            val tarea = call.receive<Tarea>()
            val nueva = tarea.copy(idTarea = tareaIdContador++)

            tareas.add(nueva)
            call.respond(HttpStatusCode.Created, nueva)
        }

        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)

                val tarea = tareas.find { it.idTarea == id }
                    ?: return@get call.respond(HttpStatusCode.NotFound)

                call.respond(tarea)
            }

            put {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)

                val tarea = call.receive<Tarea>()
                val index = tareas.indexOfFirst { it.idTarea == id }
                if (index == -1) return@put call.respond(HttpStatusCode.NotFound)

                tareas[index] = tarea.copy(idTarea = id)
                call.respond(tareas[index])
            }

            delete {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)

                val eliminado = tareas.removeIf { it.idTarea == id }

                if (!eliminado) return@delete call.respond(HttpStatusCode.NotFound)

                call.respond(HttpStatusCode.NoContent)
            }

            patch("/estado") {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest)

                val request = call.receive<EstadoRequest>()
                val index = tareas.indexOfFirst { it.idTarea == id }

                if (index == -1) return@patch call.respond(HttpStatusCode.NotFound)

                tareas[index] = tareas[index].copy(estado = request.estado)
                call.respond(tareas[index])
            }
        }
    }
}