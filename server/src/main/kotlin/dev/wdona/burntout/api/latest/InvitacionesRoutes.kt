package dev.wdona.burntout.api.latest

import dev.wdona.burntout.db.DatabaseFactory.dbQuery
import dev.wdona.burntout.db.tables.InvitacionesTable
import dev.wdona.burntout.db.tables.OrganizacionesTable
import dev.wdona.burntout.db.tables.UsuariosTable
import dev.wdona.burntout.shared.domain.GenerarInvitacionRequest
import dev.wdona.burntout.shared.domain.InvitacionCode
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.origin
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun Route.invitacionesRoutes() {
    route("/invitaciones") {
        post {
            val request = call.receive<GenerarInvitacionRequest>()
            println("[${call.request.origin.remoteHost}] POST /invitaciones idAdmin=${request.idUsuarioAdmin}")

            val result = dbQuery {
                val adminRow = UsuariosTable.selectAll()
                    .where { (UsuariosTable.id eq request.idUsuarioAdmin) and (UsuariosTable.isDeleted eq false) }
                    .singleOrNull()
                    ?: return@dbQuery null to "Usuario no encontrado"

                if (adminRow[UsuariosTable.rol] != "ADMIN") {
                    return@dbQuery null to "Solo los admins pueden generar códigos"
                }

                val idOrg = adminRow[UsuariosTable.idOrganizacion]
                val orgNombre = OrganizacionesTable.selectAll()
                    .where { (OrganizacionesTable.id eq idOrg) and (OrganizacionesTable.isDeleted eq false) }
                    .single()[OrganizacionesTable.nombre]

                val code = generarCodigo(orgNombre)
                val ahora = System.currentTimeMillis()

                InvitacionesTable.insert {
                    it[InvitacionesTable.code] = code
                    it[InvitacionesTable.idOrganizacion] = idOrg
                    it[InvitacionesTable.rol] = request.rol
                    it[InvitacionesTable.creadoPor] = request.idUsuarioAdmin
                    it[InvitacionesTable.creadoEn] = ahora
                    it[InvitacionesTable.expiraEn] = request.expiraEn
                }

                InvitacionCode(
                    code = code,
                    idOrganizacion = idOrg,
                    rol = request.rol,
                    creadoPor = request.idUsuarioAdmin,
                    creadoEn = ahora,
                    expiraEn = request.expiraEn
                ) to null
            }

            val (invitacion, error) = result
            if (error != null) {
                call.respond(HttpStatusCode.Forbidden, error)
            } else {
                call.respond(HttpStatusCode.Created, invitacion!!)
            }
        }

        get {
            val idOrg = call.request.queryParameters["idOrg"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)
            val idAdmin = call.request.queryParameters["idUsuarioAdmin"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)
            println("[${call.request.origin.remoteHost}] GET /invitaciones idOrg=$idOrg idAdmin=$idAdmin")

            val result = dbQuery {
                val adminRow = UsuariosTable.selectAll()
                    .where { (UsuariosTable.id eq idAdmin) and (UsuariosTable.idOrganizacion eq idOrg) and (UsuariosTable.isDeleted eq false) }
                    .singleOrNull()
                    ?: return@dbQuery null

                if (adminRow[UsuariosTable.rol] != "ADMIN") return@dbQuery null

                InvitacionesTable.selectAll()
                    .where { InvitacionesTable.idOrganizacion eq idOrg }
                    .map {
                        InvitacionCode(
                            code = it[InvitacionesTable.code],
                            idOrganizacion = it[InvitacionesTable.idOrganizacion],
                            rol = it[InvitacionesTable.rol],
                            creadoPor = it[InvitacionesTable.creadoPor],
                            creadoEn = it[InvitacionesTable.creadoEn],
                            expiraEn = it[InvitacionesTable.expiraEn],
                            usadoEn = it[InvitacionesTable.usadoEn],
                            usadoPor = it[InvitacionesTable.usadoPor]
                        )
                    }
            }

            if (result == null) {
                call.respond(HttpStatusCode.Forbidden, "No autorizado")
            } else {
                call.respond(result)
            }
        }
    }
}

private fun generarCodigo(nombreOrg: String): String {
    val prefix = nombreOrg.uppercase()
        .filter { it.isLetterOrDigit() }
        .take(8)
        .ifEmpty { "ORG" }
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    val suffix = (1..6).map { chars.random() }.joinToString("")
    return "$prefix-$suffix"
}
