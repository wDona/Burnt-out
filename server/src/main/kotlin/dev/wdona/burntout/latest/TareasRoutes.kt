package dev.wdona.burntout.latest

import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.tareasRoutes() {
    route("/tareas") {
        get {

        }

        post {

        }

        route("/{id}") {
            get {

            }

            put {

            }

            delete {

            }

            patch("/estado") {

            }
        }
    }
}