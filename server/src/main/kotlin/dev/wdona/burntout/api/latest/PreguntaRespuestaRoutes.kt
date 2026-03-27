package dev.wdona.burntout.api.latest
import dev.wdona.burntout.db.DatabaseFactory.dbQuery
import dev.wdona.burntout.db.tables.PreguntasTable
import dev.wdona.burntout.db.tables.RespuestasTable
import dev.wdona.burntout.domain.model.Respuesta
import dev.wdona.burntout.shared.domain.Pregunta
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
fun Route.preguntasRespuestasRoutes() {
    route("/preguntas") {
        get {
            val idOrg = call.request.queryParameters["idOrg"]?.toLongOrNull()
            val resultado = dbQuery {
                val query = if (idOrg != null) {
                    PreguntasTable .selectAll().where { PreguntasTable.idOrganizacion eq idOrg }
                } else {
                    PreguntasTable.selectAll()
                }
                query.map {
                    Pregunta(
                        idPregunta = it[PreguntasTable.id],
                        pregunta = it[PreguntasTable.pregunta],
                        idOrganizacion = it[PreguntasTable.idOrganizacion],
                        categoria = it[PreguntasTable.categoria]
                    )
                }
            }
            call.respond(resultado)
        }
        post {
            val pregunta = call.receive<Pregunta>()
            val nuevaId = dbQuery {
                PreguntasTable.insert {
                    it[PreguntasTable.pregunta] = pregunta.pregunta
                    it[idOrganizacion] = pregunta.idOrganizacion
                    it[categoria] = pregunta.categoria
                }[PreguntasTable.id]
            }
            call.respond(HttpStatusCode.Created, pregunta.copy(idPregunta = nuevaId))
        }
        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                val pregunta = dbQuery {
                    PreguntasTable .selectAll().where { PreguntasTable.id eq id }.map {
                        Pregunta(
                            idPregunta = it[PreguntasTable.id],
                            pregunta = it[PreguntasTable.pregunta],
                            idOrganizacion = it[PreguntasTable.idOrganizacion],
                            categoria = it[PreguntasTable.categoria]
                        )
                    }.singleOrNull()
                } ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(pregunta)
            }
            put {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)
                val pregunta = call.receive<Pregunta>()
                val updatedCount = dbQuery {
                    PreguntasTable.update({ PreguntasTable.id eq id }) {
                        it[PreguntasTable.pregunta] = pregunta.pregunta
                        it[idOrganizacion] = pregunta.idOrganizacion
                        it[categoria] = pregunta.categoria
                    }
                }
                if (updatedCount == 0) return@put call.respond(HttpStatusCode.NotFound)
                call.respond(pregunta.copy(idPregunta = id))
            }
            delete {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)
                val deletedCount = dbQuery {
                    PreguntasTable.deleteWhere { PreguntasTable.id eq id }
                }
                if (deletedCount == 0) return@delete call.respond(HttpStatusCode.NotFound)
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
            val resultado = dbQuery {
                val query = RespuestasTable.selectAll()
                if (idPregunta != null) {
                    query.andWhere { RespuestasTable.idPregunta eq idPregunta }
                }
                if (idUsuario != null) {
                    query.andWhere { RespuestasTable.idUsuario eq idUsuario }
                }
                if (fecha != null) {
                    query.andWhere { RespuestasTable.fecha eq fecha }
                }
                var results = query.map {
                    Respuesta(
                        idUsuario = it[RespuestasTable.idUsuario],
                        idPregunta = it[RespuestasTable.idPregunta],
                        anonimo = it[RespuestasTable.anonimo],
                        respuesta = it[RespuestasTable.respuesta],
                        nombreUsuario = it[RespuestasTable.nombreUsuario],
                        fecha = it[RespuestasTable.fecha]
                    )
                }
                if (last && idUsuario != null) {
                    val ultimaFecha = results.maxOfOrNull { it.fecha ?: 0L }
                    if (ultimaFecha != null) {
                        results = results.filter { it.fecha == ultimaFecha }
                    }
                }
                results
            }
            call.respond(resultado)
        }
        post {
            val respuesta = call.receive<Respuesta>()
            dbQuery {
                // Delete if exists to act as insert/replace on PK or just use update if not using a specific UPSERT
                val existing = RespuestasTable .selectAll().where {
                    (RespuestasTable.idUsuario eq respuesta.idUsuario) and (RespuestasTable.idPregunta eq respuesta.idPregunta)
                }.count() > 0
                if (existing) {
                    RespuestasTable.update({ (RespuestasTable.idUsuario eq respuesta.idUsuario) and (RespuestasTable.idPregunta eq respuesta.idPregunta) }) {
                        it[anonimo] = respuesta.anonimo
                        it[RespuestasTable.respuesta] = respuesta.respuesta
                        it[nombreUsuario] = respuesta.nombreUsuario
                        it[fecha] = respuesta.fecha
                    }
                } else {
                    RespuestasTable.insert {
                        it[idUsuario] = respuesta.idUsuario
                        it[idPregunta] = respuesta.idPregunta
                        it[anonimo] = respuesta.anonimo
                        it[RespuestasTable.respuesta] = respuesta.respuesta
                        it[nombreUsuario] = respuesta.nombreUsuario
                        it[fecha] = respuesta.fecha
                    }
                }
            }
            call.respond(HttpStatusCode.Created, respuesta)
        }
    }
}
