package dev.wdona.burntout.latest

import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.ajustesRoutes() {
    route("/ajustes/{idUsuario}") {
        get {

        }

        put("/{id}") {

        }
    }


}