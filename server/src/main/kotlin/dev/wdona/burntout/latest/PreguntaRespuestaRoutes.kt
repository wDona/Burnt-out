package dev.wdona.burntout.latest

import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.preguntasRespuestasRoutes() {
    route("/preguntas") {
        get {

        }

        post {

        }

        route("/{id}") {
            get {

            }

            delete {

            }
        }
    }

    route("/respuestas") {
        get {

        }

        post {

        }
    }
}