package dev.wdona.burntout.data.repository

import dev.wdona.burntout.data.dao.TareaRepository
import dev.wdona.burntout.data.datasource.local.OperacionPendienteLocalDataSource
import dev.wdona.burntout.data.datasource.local.TareaLocalDataSource
import dev.wdona.burntout.data.datasource.mapper.TareaMapper
import dev.wdona.burntout.data.datasource.remote.TareaRemoteDataSource
import dev.wdona.burntout.domain.entity.Entity
import dev.wdona.burntout.domain.model.TipoAccion
import dev.wdona.burntout.shared.domain.Tarea
import dev.wdona.burntout.shared.utils.SettingsManager
import dev.wdona.burntout.shared.utils.getCurrentTimestampSeconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class TareaRepositoryImpl(
    private val local: TareaLocalDataSource,
    private val remote: TareaRemoteDataSource,
    private val pendiente: OperacionPendienteLocalDataSource
) : TareaRepository {

    override suspend fun getTareasByTableroId(tableroId: String): List<Tarea> = withContext(NonCancellable + Dispatchers.IO) {
        if (!SettingsManager.isUsuarioInvitado()) {
            try {
                val serverTareas = remote.getTareasByTablero(tableroId)
                val localMap = local.getTareasByTablero(tableroId).associateBy { it.idTarea }
                serverTareas.forEach { serverTarea ->
                    val localTarea = localMap[serverTarea.idTarea]
                    if (localTarea == null || serverTarea.updatedAt >= localTarea.updatedAt) {
                        local.insertOrUpdateTarea(serverTarea)
                    }
                }
            } catch (e: Exception) {
                println("Servidor offline (getTareas): ${e.message}")
            }
        }
        local.getTareasByTablero(tableroId)
    }

    override suspend fun getTareaById(idTarea: String, idTablero: String): Tarea? = withContext(NonCancellable + Dispatchers.IO) {
        val tareaLocal = local.getTareaById(idTarea, idTablero)

        if (SettingsManager.isUsuarioInvitado()) {
            return@withContext tareaLocal
        }

        try {
            val tareaRemota = remote.getTareaById(idTarea, idTablero)
            if (tareaRemota != null) {
                local.insertOrUpdateTarea(tareaRemota)
            }
            tareaRemota ?: tareaLocal
        } catch (e: Exception) {
            // Si el servidor responde 404 pero tenemos cache local, seguimos con ella.
            println("Servidor offline (getTareaById): ${e.message}")
            tareaLocal
        }
    }

    override suspend fun crearTarea(tarea: Tarea) {
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                local.crearTarea(tarea)
            } catch (e: Exception) {
                println("Error local al crear tarea: ${e.message}")
            }
            if (SettingsManager.isUsuarioInvitado()) return@withContext
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.CREACION.getNombreAccion(),
                    Entity.TAREA.getNombreEntity(),
                    tarea.idTarea,
                    TareaMapper.toJson(tarea),
                    getCurrentTimestampSeconds(),
                    0L
                )
            } catch (e: Exception) {
                println("Error al registrar operacion pendiente: ${e.message}")
            }
        }
    }

    override suspend fun actualizarTarea(tarea: Tarea) {
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                local.actualizarTarea(tarea)
            } catch (e: Exception) {
                println("Error local al actualizar tarea: ${e.message}")
            }
            if (SettingsManager.isUsuarioInvitado()) return@withContext
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.ACTUALIZACION.getNombreAccion(),
                    Entity.TAREA.getNombreEntity(),
                    tarea.idTarea,
                    TareaMapper.toJson(tarea),
                    getCurrentTimestampSeconds(),
                    0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }

    override suspend fun eliminarTarea(idTarea: String) {
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                local.eliminarTarea(idTarea)
            } catch (e: Exception) {
                println("Error local al eliminar tarea: ${e.message}")
            }
            if (SettingsManager.isUsuarioInvitado()) return@withContext
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.ELIMINACION.getNombreAccion(),
                    Entity.TAREA.getNombreEntity(),
                    idTarea,
                    "{\"idTarea\":\"$idTarea\"}",
                    getCurrentTimestampSeconds(),
                    0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }
}