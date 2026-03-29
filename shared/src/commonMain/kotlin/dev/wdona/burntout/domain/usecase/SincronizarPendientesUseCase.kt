package dev.wdona.burntout.domain.usecase

import com.russhwolf.settings.Settings
import dev.wdona.burntout.data.datasource.mapper.AjusteMapper
import dev.wdona.burntout.data.datasource.remote.AjusteRemoteDataSource
import dev.wdona.burntout.data.datasource.remote.EquipoRemoteDataSource
import dev.wdona.burntout.data.datasource.remote.OrganizacionRemoteDataSource
import dev.wdona.burntout.data.datasource.remote.PreguntaRespuestaRemoteDataSource
import dev.wdona.burntout.data.datasource.remote.SubtareaRemoteDataSource
import dev.wdona.burntout.data.datasource.remote.TableroRemoteDataSource
import dev.wdona.burntout.data.datasource.remote.TareaRemoteDataSource
import dev.wdona.burntout.data.datasource.remote.UsuarioRemoteDataSource
import dev.wdona.burntout.domain.entity.Entity
import dev.wdona.burntout.domain.model.OperacionPendiente
import dev.wdona.burntout.domain.model.Respuesta
import dev.wdona.burntout.domain.model.TipoAccion
import dev.wdona.burntout.domain.repository.OperacionesPendientesRepository
import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.domain.Organizacion
import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.shared.domain.Subtarea
import dev.wdona.burntout.shared.domain.Tablero
import dev.wdona.burntout.shared.domain.Tarea
import dev.wdona.burntout.shared.domain.Usuario
import dev.wdona.burntout.domain.model.Ajuste
import dev.wdona.burntout.shared.utils.SettingsManager
import kotlinx.serialization.json.Json

class SincronizarPendientesUseCase(
    private val pendientesRepository: OperacionesPendientesRepository,
    private val tareaRemote: TareaRemoteDataSource,
    private val tableroRemote: TableroRemoteDataSource,
    private val equipoRemote: EquipoRemoteDataSource,
    private val usuarioRemote: UsuarioRemoteDataSource,
    private val preguntaRespuestaRemote: PreguntaRespuestaRemoteDataSource,
    private val ajusteRemote: AjusteRemoteDataSource,
    private val subtareaRemote: SubtareaRemoteDataSource,
    private val organizacionRemote: OrganizacionRemoteDataSource
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true }

    suspend operator fun invoke(): Boolean {
        val pendientes = pendientesRepository.getOperacionesPendientes()
        if (pendientes.isEmpty()) {
            SettingsManager.setSincronizadoEnEstaApertura(true)
            return true
        }

        var todoOk = true
        for (op in pendientes) {
            val exito = try {
                println("Operación ${op.idAccion} (${op.tablaAfectada}) ejecutada correctamente")
                ejecutarOperacion(op)
            } catch (e: Exception) {
                println("Error al sincronizar op ${op.idAccion} (${op.tablaAfectada}): ${e.message}")
                false
            }
            if (exito) {
                pendientesRepository.cambiarEstadoOperacion(1L, op.idAccion)
            } else {
                todoOk = false
            }
        }
        println("Sincronizacion completada?: $todoOk")
        SettingsManager.setSincronizadoEnEstaApertura(todoOk)
        return todoOk
    }

    private suspend fun ejecutarOperacion(op: OperacionPendiente): Boolean {
        return when (op.tablaAfectada) {
            Entity.TAREA.getNombreEntity() -> ejecutarTarea(op)
            Entity.TABLERO.getNombreEntity() -> ejecutarTablero(op)
            Entity.EQUIPO.getNombreEntity() -> ejecutarEquipo(op)
            Entity.USUARIO.getNombreEntity() -> ejecutarUsuario(op)
            Entity.PREGUNTA.getNombreEntity() -> ejecutarPregunta(op)
            Entity.AJUSTE.getNombreEntity(), Entity.AJUSTE_USER.getNombreEntity() -> {
                ejecutarAjuste(op)
            }
            Entity.SUBTAREA.getNombreEntity() -> ejecutarSubtarea(op)
            Entity.ORGANIZACION.getNombreEntity() -> ejecutarOrganizacion(op)
            Entity.RESPONDER.getNombreEntity() -> ejecutarRespuesta(op)
            else -> true
        }
    }

    private suspend fun ejecutarTarea(op: OperacionPendiente): Boolean {
        return when (op.tipoAccion) {
            TipoAccion.CREACION.getNombreAccion() -> {
                val tarea = json.decodeFromString<Tarea>(op.datosJson)
                tareaRemote.crearTarea(tarea) != -1L
            }
            TipoAccion.ACTUALIZACION.getNombreAccion() -> {
                val tarea = json.decodeFromString<Tarea>(op.datosJson)
                tareaRemote.actualizarTarea(tarea)
            }
            TipoAccion.ELIMINACION.getNombreAccion() -> tareaRemote.eliminarTarea(op.idAfectado)
            else -> true
        }
    }

    private suspend fun ejecutarTablero(op: OperacionPendiente): Boolean {
        return when (op.tipoAccion) {
            TipoAccion.CREACION.getNombreAccion() -> {
                val tablero = json.decodeFromString<Tablero>(op.datosJson)
                tableroRemote.crearTablero(tablero)
            }
            TipoAccion.ACTUALIZACION.getNombreAccion() -> {
                val tablero = json.decodeFromString<Tablero>(op.datosJson)
                tableroRemote.actualizarTablero(tablero)
            }
            TipoAccion.ELIMINACION.getNombreAccion() -> tableroRemote.eliminarTablero(op.idAfectado)
            else -> true
        }
    }

    private suspend fun ejecutarEquipo(op: OperacionPendiente): Boolean {
        return when (op.tipoAccion) {
            TipoAccion.CREACION.getNombreAccion() -> {
                val equipo = json.decodeFromString<Equipo>(op.datosJson)
                equipoRemote.crearEquipo(equipo)
            }
            TipoAccion.ACTUALIZACION.getNombreAccion() -> {
                val equipo = json.decodeFromString<Equipo>(op.datosJson)
                equipoRemote.actualizarEquipo(equipo)
            }
            TipoAccion.ELIMINACION.getNombreAccion() -> equipoRemote.eliminarEquipo(op.idAfectado)
            else -> true
        }
    }

    private suspend fun ejecutarUsuario(op: OperacionPendiente): Boolean {
        return when (op.tipoAccion) {
            TipoAccion.CREACION.getNombreAccion() -> {
                val usuario = json.decodeFromString<Usuario>(op.datosJson)
                usuarioRemote.crearUsuario(usuario) != -1L
            }
            TipoAccion.ACTUALIZACION.getNombreAccion() -> {
                val usuario = json.decodeFromString<Usuario>(op.datosJson)
                usuarioRemote.actualizarUsuario(usuario)
            }
            TipoAccion.ELIMINACION.getNombreAccion() -> usuarioRemote.eliminarUsuario(op.idAfectado)
            else -> true
        }
    }

    private suspend fun ejecutarPregunta(op: OperacionPendiente): Boolean {
        return when (op.tipoAccion) {
            TipoAccion.CREACION.getNombreAccion() -> {
                val pregunta = json.decodeFromString<Pregunta>(op.datosJson)
                preguntaRespuestaRemote.crearPregunta(pregunta) != -1L
            }
            TipoAccion.ACTUALIZACION.getNombreAccion() -> {
                val pregunta = json.decodeFromString<Pregunta>(op.datosJson)
                preguntaRespuestaRemote.actualizarPregunta(pregunta)
            }
            TipoAccion.ELIMINACION.getNombreAccion() -> preguntaRespuestaRemote.eliminarPregunta(op.idAfectado)
            else -> true
        }
    }

    private suspend fun ejecutarAjuste(op: OperacionPendiente): Boolean {
        return try {
            val ajuste = json.decodeFromString<Ajuste>(op.datosJson)
            val idUsuario = SettingsManager.getIdUsuarioActual()
            ajusteRemote.modificarAjuste(idUsuario, ajuste)
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun ejecutarSubtarea(op: OperacionPendiente): Boolean {
        return when (op.tipoAccion) {
            TipoAccion.CREACION.getNombreAccion() -> {
                val subtarea = json.decodeFromString<Subtarea>(op.datosJson)
                subtareaRemote.crearSubtarea(subtarea) != -1L
            }
            TipoAccion.ACTUALIZACION.getNombreAccion() -> {
                val subtarea = json.decodeFromString<Subtarea>(op.datosJson)
                subtareaRemote.actualizarSubtarea(subtarea)
            }
            TipoAccion.ELIMINACION.getNombreAccion() -> subtareaRemote.eliminarSubtarea(op.idAfectado)
            else -> true
        }
    }

    private suspend fun ejecutarOrganizacion(op: OperacionPendiente): Boolean {
        return when (op.tipoAccion) {
            TipoAccion.CREACION.getNombreAccion() -> {
                val org = json.decodeFromString<Organizacion>(op.datosJson)
                organizacionRemote.crearOrganizacion(org) != -1L
            }
            TipoAccion.ACTUALIZACION.getNombreAccion() -> {
                val org = json.decodeFromString<Organizacion>(op.datosJson)
                organizacionRemote.actualizarOrganizacion(org)
            }
            TipoAccion.ELIMINACION.getNombreAccion() -> organizacionRemote.eliminarOrganizacion(op.idAfectado)
            else -> true
        }
    }

    private suspend fun ejecutarRespuesta(op: OperacionPendiente): Boolean {
        return try {
            val respuesta = json.decodeFromString<Respuesta>(op.datosJson)
            preguntaRespuestaRemote.responderPregunta(respuesta)
            true
        } catch (e: Exception) {
            false
        }
    }
}
