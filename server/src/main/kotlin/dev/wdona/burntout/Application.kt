package dev.wdona.burntout

import dev.wdona.burntout.latest.ajustesRoutes
import dev.wdona.burntout.latest.equiposRoutes
import dev.wdona.burntout.latest.organizacionesRoutes
import dev.wdona.burntout.latest.preguntasRespuestasRoutes
import dev.wdona.burntout.latest.subtareasRoutes
import dev.wdona.burntout.latest.tablerosRoutes
import dev.wdona.burntout.latest.tareasRoutes
import dev.wdona.burntout.latest.usuariosRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.*

fun main() {
    embeddedServer(
        Netty,
        port = SERVER_PORT,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    // TODO: USAR Exposed (ORM de jetbrains) para db en lugar de sqdelight

    routing {
        // v1
        route("/api/v1") {
            ajustesRoutes()
            equiposRoutes()
            organizacionesRoutes()
            preguntasRespuestasRoutes()
            subtareasRoutes()
            tablerosRoutes()
            tareasRoutes()
            usuariosRoutes()
        }

        // Latest
        route(API_PATH) {
            ajustesRoutes()
            equiposRoutes()
            organizacionesRoutes()
            preguntasRespuestasRoutes()
            subtareasRoutes()
            tablerosRoutes()
            tareasRoutes()
            usuariosRoutes()
        }
    }
}