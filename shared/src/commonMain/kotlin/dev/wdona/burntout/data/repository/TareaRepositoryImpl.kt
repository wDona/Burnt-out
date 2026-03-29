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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TareaRepositoryImpl(
    private val local: TareaLocalDataSource,
    private val remote: TareaRemoteDataSource,
    private val pendiente: OperacionPendienteLocalDataSource
) : TareaRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Default)

    override suspend fun getTareasByTableroId(tableroId: Long): List<Tarea> = withContext(NonCancellable + Dispatchers.IO) {
        if (!SettingsManager.isUsuarioInvitado()) {
            repositoryScope.launch {
                try {
                    val tareas = remote.getTareasByTablero(tableroId)
                    local.eliminarTareasPorTablero(tableroId)
                    tareas.forEach { local.crearTarea(it) }
                } catch (e: Exception) {
                    println("Servidor offline (getTareas): ${e.message}")
                }
            }
        }
        local.getTareasByTablero(tableroId)
    }

    override suspend fun getTareaById(idTarea: Long, idTablero: Long): Tarea? = withContext(NonCancellable + Dispatchers.IO) {
        if (!SettingsManager.isUsuarioInvitado()) {
            repositoryScope.launch {
                try {
                    val tarea = remote.getTareaById(idTarea, idTablero)
                    local.eliminarTarea(tarea.idTarea)
                    local.crearTarea(tarea)
                } catch (e: Exception) {
                    println("Servidor offline (getTareaById): ${e.message}")
                }
            }
        }
        local.getTareaById(idTarea, idTablero)
    }

    override suspend fun crearTarea(tarea: Tarea) {
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                local.crearTarea(tarea)
            } catch (e: Exception) {
                println("Error local al crear tarea: ${e.message}")
            }
        }

        if (SettingsManager.isUsuarioInvitado()) return

        withContext(NonCancellable + Dispatchers.IO) {
            var exitoRemoto = false
            var idRemoto: Long = -1
            try {
                idRemoto = remote.crearTarea(tarea)
                exitoRemoto = idRemoto != -1L
            } catch (e: Exception) {
                println("Servidor offline al crear tarea: ${e.message}")
            }
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.CREACION.getNombreAccion(),
                    Entity.TAREA.getNombreEntity(),
                    if (exitoRemoto) idRemoto else 0L,
                    TareaMapper.toJson(tarea),
                    System.currentTimeMillis(),
                    if (exitoRemoto) 1L else 0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
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
        }

        if (SettingsManager.isUsuarioInvitado()) return

        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            try {
                exito = remote.actualizarTarea(tarea)
            } catch (e: Exception) {
                println("Servidor offline al actualizar tarea: ${e.message}")
            }
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.ACTUALIZACION.getNombreAccion(),
                    Entity.TAREA.getNombreEntity(),
                    tarea.idTarea,
                    TareaMapper.toJson(tarea),
                    System.currentTimeMillis(),
                    if (exito) 1L else 0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }

    override suspend fun eliminarTarea(idTarea: Long) {
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                local.eliminarTarea(idTarea)
            } catch (e: Exception) {
                println("Error local al eliminar tarea: ${e.message}")
            }
        }

        if (SettingsManager.isUsuarioInvitado()) return

        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            try {
                exito = remote.eliminarTarea(idTarea)
            } catch (e: Exception) {
                println("Servidor offline al eliminar tarea: ${e.message}")
            }
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.ELIMINACION.getNombreAccion(),
                    Entity.TAREA.getNombreEntity(),
                    idTarea,
                    "",
                    System.currentTimeMillis(),
                    if (exito) 1L else 0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }
}