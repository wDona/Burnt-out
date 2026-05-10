package dev.wdona.burntout

import dev.wdona.burntout.api.latest.syncRoutes
import dev.wdona.burntout.api.latest.ajustesRoutes
import dev.wdona.burntout.api.latest.invitacionesRoutes
import dev.wdona.burntout.api.latest.equiposRoutes
import dev.wdona.burntout.api.latest.organizacionesRoutes
import dev.wdona.burntout.api.latest.preguntasRespuestasRoutes
import dev.wdona.burntout.api.latest.sesionesRoutes
import dev.wdona.burntout.api.latest.subtareasRoutes
import dev.wdona.burntout.api.latest.tablerosRoutes
import dev.wdona.burntout.api.latest.tareasRoutes
import dev.wdona.burntout.api.latest.usuariosRoutes
import dev.wdona.burntout.db.tables.SesionesTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import dev.wdona.burntout.commands.comandoHandler
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.origin
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import dev.wdona.burntout.db.DatabaseFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    intercept(ApplicationCallPipeline.Call) {
        val ip = call.request.origin.remoteHost
        val method = call.request.httpMethod.value
        val uri = call.request.uri
        println("[$ip] Peticion a $uri con metodo $method")
        if (!RateLimiter.allow(ip)) {
            println("[$ip] Rate limit superado — 429")
            call.respond(HttpStatusCode.TooManyRequests, "Rate limit: max 120 req/min por IP")
            return@intercept finish()
        }

        val esPublico = (method == "POST" && (uri.endsWith("/usuarios") || uri.contains("/usuarios/login"))) ||
                        (method == "GET" && (uri.contains("/usuarios/existe/") || (uri.contains("/invitaciones/") && !uri.endsWith("/invitaciones"))))

        if (!esPublico) {
            val authHeader = call.request.headers["Authorization"]
            val token = authHeader?.removePrefix("Bearer ")?.trim()
            if (token.isNullOrBlank()) {
                println("[$ip] Sin token — 401")
                call.respond(HttpStatusCode.Unauthorized, "Token requerido")
                return@intercept finish()
            }
            val sesionValida = DatabaseFactory.dbQuery {
                SesionesTable.selectAll().where { SesionesTable.token eq token }.count() > 0
            }
            if (!sesionValida) {
                println("[$ip] Token inválido — 401")
                call.respond(HttpStatusCode.Unauthorized, "Token inválido o sesión expirada")
                return@intercept finish()
            }
        }
    }

    DatabaseFactory.init()

    routing {
        // v1
        route("/api/v1") {
            syncRoutes()
            ajustesRoutes()
            equiposRoutes()
            invitacionesRoutes()
            organizacionesRoutes()
            preguntasRespuestasRoutes()
            sesionesRoutes()
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

    launch(Dispatchers.IO) {
        System.`in`.bufferedReader().lineSequence().forEach {
            val comando = it.trim().split(" ")
            if (comando.isNotEmpty()) {
                val cmd = comando[0]
                val args = if (comando.size > 1) comando.subList(1, comando.size) else emptyList()
                comandoHandler(cmd, args)
            }
        }
    }
}