package dev.wdona.burntout.api.latest

import dev.wdona.burntout.db.DatabaseFactory.dbQuery
import dev.wdona.burntout.db.tables.SesionesTable
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

fun Route.sesionesRoutes() {
    route("/sesiones") {
        delete("/{token}") {
            val token = call.parameters["token"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest)
            dbQuery {
                SesionesTable.deleteWhere { SesionesTable.token eq token }
            }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
