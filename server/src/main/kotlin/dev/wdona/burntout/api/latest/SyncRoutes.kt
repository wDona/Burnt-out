package dev.wdona.burntout.api.latest

import dev.wdona.burntout.data.api.SyncPullRequest
import dev.wdona.burntout.data.api.SyncResponse
import dev.wdona.burntout.db.DatabaseFactory.dbQuery
import dev.wdona.burntout.db.tables.*
import dev.wdona.burntout.shared.domain.*
import dev.wdona.burntout.domain.model.Respuesta
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*

fun Route.syncRoutes() {
    route("/sync") {
        post("/pull") {
            val req = call.receive<SyncPullRequest>()
            val serverTimestamp = System.currentTimeMillis()

            val response = dbQuery {
                val tareas = TareasTable.selectAll()
                    .where { (TareasTable.updatedAt greater req.lastSyncTimestamp) }
                    .map { row ->
                        Tarea(
                            idTarea = row[TareasTable.id],
                            titulo = row[TareasTable.titulo],
                            descripcion = row[TareasTable.descripcion],
                            estado = row[TareasTable.estado],
                            idTableroPerteneciente = row[TareasTable.idTablero],
                            idUsuarioAsignado = row[TareasTable.idUsuarioAsignado],
                            idSubtareas = null, // Se puede poblar si es necesario, pero el cliente las recibirá por separado
                            fechaVencimiento = row[TareasTable.fechaVencimiento],
                            isDeleted = row[TareasTable.isDeleted],
                            updatedAt = row[TareasTable.updatedAt]
                        )
                    }

                val subtareas = SubtareasTable.selectAll()
                    .where { (SubtareasTable.updatedAt greater req.lastSyncTimestamp) }
                    .map { row ->
                        Subtarea(
                            idSubtarea = row[SubtareasTable.id],
                            titulo = row[SubtareasTable.titulo],
                            descripcion = row[SubtareasTable.descripcion],
                            completado = row[SubtareasTable.completado],
                            idTareaPerteneciente = row[SubtareasTable.idTarea],
                            isDeleted = row[SubtareasTable.isDeleted],
                            updatedAt = row[SubtareasTable.updatedAt]
                        )
                    }

                val respuestas = RespuestasTable.selectAll()
                    .where { (RespuestasTable.updatedAt greater req.lastSyncTimestamp) }
                    .map { row ->
                        Respuesta(
                            idRespuesta = row[RespuestasTable.id],
                            idUsuario = row[RespuestasTable.idUsuario],
                            idPregunta = row[RespuestasTable.idPregunta],
                            anonimo = row[RespuestasTable.anonimo],
                            respuesta = row[RespuestasTable.respuesta],
                            nombreUsuario = row[RespuestasTable.nombreUsuario],
                            fecha = row[RespuestasTable.fecha],
                            isDeleted = row[RespuestasTable.isDeleted],
                            updatedAt = row[RespuestasTable.updatedAt]
                        )
                    }

                val preguntas = PreguntasTable.selectAll()
                    .where { (PreguntasTable.updatedAt greater req.lastSyncTimestamp) }
                    .map { row ->
                        Pregunta(
                            idPregunta = row[PreguntasTable.id],
                            pregunta = row[PreguntasTable.pregunta],
                            idOrganizacion = row[PreguntasTable.idOrganizacion],
                            categoria = row[PreguntasTable.categoria],
                            isDeleted = row[PreguntasTable.isDeleted],
                            updatedAt = row[PreguntasTable.updatedAt]
                        )
                    }

                val tableros = TablerosTable.selectAll()
                    .where { (TablerosTable.updatedAt greater req.lastSyncTimestamp) }
                    .map { row ->
                        Tablero(
                            idTablero = row[TablerosTable.id],
                            titulo = row[TablerosTable.titulo],
                            idOrganizacion = row[TablerosTable.idOrganizacion],
                            idEquipo = row[TablerosTable.idEquipo],
                            isDeleted = row[TablerosTable.isDeleted],
                            updatedAt = row[TablerosTable.updatedAt]
                        )
                    }

                val equipos = EquiposTable.selectAll()
                    .where { (EquiposTable.updatedAt greater req.lastSyncTimestamp) }
                    .map { row ->
                        val idEq = row[EquiposTable.id]
                        val miembros = EquipoMiembrosTable.selectAll()
                            .where { (EquipoMiembrosTable.idEquipo eq idEq) and (EquipoMiembrosTable.isDeleted eq false) }
                            .map { it[EquipoMiembrosTable.idMiembro] }
                        Equipo(
                            idEquipo = idEq,
                            titulo = row[EquiposTable.titulo],
                            puntuacion = row[EquiposTable.puntuacion],
                            idOrganizacion = row[EquiposTable.idOrganizacion],
                            idMiembros = miembros,
                            isDeleted = row[EquiposTable.isDeleted],
                            updatedAt = row[EquiposTable.updatedAt]
                        )
                    }

                val usuarios = UsuariosTable.selectAll()
                    .where { (UsuariosTable.updatedAt greater req.lastSyncTimestamp) }
                    .map { row ->
                        Usuario(
                            idUsuario = row[UsuariosTable.id],
                            username = row[UsuariosTable.username],
                            password = row[UsuariosTable.password],
                            nombre = row[UsuariosTable.nombre],
                            riesgoBurnout = row[UsuariosTable.riesgoBurnout],
                            descripcion = row[UsuariosTable.descripcion],
                            idOrganizacion = row[UsuariosTable.idOrganizacion],
                            idEquipo = row[UsuariosTable.idEquipo],
                            rol = row[UsuariosTable.rol],
                            isDeleted = row[UsuariosTable.isDeleted],
                            updatedAt = row[UsuariosTable.updatedAt]
                        )
                    }

                val organizaciones = OrganizacionesTable.selectAll()
                    .where { (OrganizacionesTable.updatedAt greater req.lastSyncTimestamp) }
                    .map { row ->
                        Organizacion(
                            idOrganizacion = row[OrganizacionesTable.id],
                            nombre = row[OrganizacionesTable.nombre],
                            isDeleted = row[OrganizacionesTable.isDeleted],
                            updatedAt = row[OrganizacionesTable.updatedAt]
                        )
                    }

                SyncResponse(
                    tareas = tareas,
                    subtareas = subtareas,
                    respuestas = respuestas,
                    preguntas = preguntas,
                    tableros = tableros,
                    equipos = equipos,
                    usuarios = usuarios,
                    organizaciones = organizaciones,
                    ajustes = emptyList(),
                    serverTimestamp = serverTimestamp
                )
            }
            call.respond(response)
        }
    }
}
