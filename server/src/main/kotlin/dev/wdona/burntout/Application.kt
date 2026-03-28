package dev.wdona.burntout

import dev.wdona.burntout.api.latest.ajustesRoutes
import dev.wdona.burntout.api.latest.equiposRoutes
import dev.wdona.burntout.api.latest.organizacionesRoutes
import dev.wdona.burntout.api.latest.preguntasRespuestasRoutes
import dev.wdona.burntout.api.latest.subtareasRoutes
import dev.wdona.burntout.api.latest.tablerosRoutes
import dev.wdona.burntout.api.latest.tareasRoutes
import dev.wdona.burntout.api.latest.usuariosRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.doublereceive.DoubleReceive
import io.ktor.server.plugins.origin
import io.ktor.server.request.httpMethod
import io.ktor.server.request.receiveText
import io.ktor.server.request.uri
import io.ktor.server.routing.*
import dev.wdona.burntout.db.DatabaseFactory

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
    install(DoubleReceive)

    intercept(ApplicationCallPipeline.Call) {
        val ip = call.request.origin.remoteHost
        val method = call.request.httpMethod.value
        val uri = call.request.uri
        val route = uri.substringBeforeLast("/")

        try {
            val body = call.receiveText()
            if (body.isNotBlank()) {
                println("[$ip] Peticion a $uri con metodo $method | Contenido: $body")
            } else {
                println("[$ip] Peticion a $uri con metodo $method")
            }
        } catch (e: Exception) {
            println("[$ip] Peticion a $uri con metodo $method")
        }
    }

    DatabaseFactory.init()

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

//        // Latest
//        route(API_PATH) {
//            ajustesRoutes()
//            equiposRoutes()
//            organizacionesRoutes()
//            preguntasRespuestasRoutes()
//            subtareasRoutes()
//            tablerosRoutes()
//            tareasRoutes()
//            usuariosRoutes()
//        }
    }
}