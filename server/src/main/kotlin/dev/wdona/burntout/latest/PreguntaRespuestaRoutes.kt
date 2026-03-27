package dev.wdona.burntout.latest

import dev.wdona.burntout.domain.model.Respuesta
import dev.wdona.burntout.shared.domain.Pregunta
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.preguntasRespuestasRoutes() {
    route("/preguntas") {
        get {
            val idOrg = call.request.queryParameters["idOrg"]?.toLongOrNull()

            val resultado = if (idOrg != null) preguntas.filter { it.idOrganizacion == idOrg } else preguntas

            call.respond(resultado)
        }

        post {
            val pregunta = call.receive<Pregunta>()

            val nueva = pregunta.copy(idPregunta = preguntaIdContador++)

            preguntas.add(nueva)
            call.respond(HttpStatusCode.Created, nueva)
        }

        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)

                val pregunta = preguntas.find { it.idPregunta == id }
                    ?: return@get call.respond(HttpStatusCode.NotFound)

                call.respond(pregunta)
            }

            put {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)

                val pregunta = call.receive<Pregunta>()

                val index = preguntas.indexOfFirst { it.idPregunta == id }

                if (index == -1) return@put call.respond(HttpStatusCode.NotFound)

                preguntas[index] = pregunta.copy(idPregunta = id)
                call.respond(preguntas[index])
            }

            delete {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)

                val eliminado = preguntas.removeIf { it.idPregunta == id }

                if (!eliminado) return@delete call.respond(HttpStatusCode.NotFound)

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }

    route("/respuestas") {
        get {
            val idPregunta = call.request.queryParameters["idPregunta"]?.toLongOrNull()
            val idUsuario = call.request.queryParameters["idUsuario"]?.toLongOrNull()
            val last = call.request.queryParameters["last"]?.toBoolean() ?: false
            val fecha = call.request.queryParameters["fecha"]?.toLongOrNull()

            var resultado = respuestas.toList()

            if (idPregunta != null) resultado = resultado.filter { it.idPregunta == idPregunta }

            if (idUsuario != null) resultado = resultado.filter { it.idUsuario == idUsuario }

            if (fecha != null) resultado = resultado.filter { it.fecha == fecha }

            if (last && idUsuario != null) {
                val ultimaFecha = resultado.maxOfOrNull { it.fecha ?: 0L }
                resultado = resultado.filter { it.fecha == ultimaFecha }
            }
            call.respond(resultado)
        }

        post {
            val respuesta = call.receive<Respuesta>()

            respuestas.add(respuesta)
            call.respond(HttpStatusCode.Created, respuesta)
        }
    }
}