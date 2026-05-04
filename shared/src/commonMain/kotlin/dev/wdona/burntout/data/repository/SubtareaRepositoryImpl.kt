package dev.wdona.burntout.data.repository

import dev.wdona.burntout.data.dao.SubtareaRepository
import dev.wdona.burntout.data.datasource.local.OperacionPendienteLocalDataSource
import dev.wdona.burntout.data.datasource.local.SubtareaLocalDataSource
import dev.wdona.burntout.data.datasource.mapper.SubtareaMapper
import dev.wdona.burntout.data.datasource.remote.SubtareaRemoteDataSource
import dev.wdona.burntout.domain.entity.Entity
import dev.wdona.burntout.domain.model.TipoAccion
import dev.wdona.burntout.shared.domain.Subtarea
import dev.wdona.burntout.shared.utils.SettingsManager
import dev.wdona.burntout.shared.utils.getCurrentTimestampSeconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubtareaRepositoryImpl(
    private val local: SubtareaLocalDataSource,
    private val remote: SubtareaRemoteDataSource,
    private val pendiente: OperacionPendienteLocalDataSource
) : SubtareaRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Default)

    override suspend fun getSubtareasByTarea(idTarea: String): List<Subtarea> = withContext(NonCancellable + Dispatchers.IO) {
        if (!SettingsManager.isUsuarioInvitado()) {
            repositoryScope.launch {
                try {
                    val subtareasRemotas = remote.getSubtareasByTarea(idTarea)
                    subtareasRemotas.forEach { local.insertOrUpdateSubtarea(it) }
                } catch (e: Exception) {
                    println("Servidor offline (getSubtareasByTarea): ${e.message}")
                }
            }
        }
        try {
            local.getSubtareasByTarea(idTarea)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun crearSubtarea(subtarea: Subtarea) {
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                local.crearSubtarea(subtarea)
            } catch (e: Exception) {
                println("Error local al crear subtarea: ${e.message}")
            }
        }

        if (SettingsManager.isUsuarioInvitado()) return

        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            var idGenerado: String = ""
            try {
                val response = remote.crearSubtarea(subtarea)
                idGenerado = response
                exito = idGenerado.isNotEmpty()
            } catch (e: Exception) {
                println("Servidor offline al crear subtarea: ${e.message}")
            }
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.CREACION.getNombreAccion(),
                    Entity.SUBTAREA.getNombreEntity(),
                    if (exito) idGenerado else subtarea.idSubtarea,
                    SubtareaMapper.toJson(subtarea),
                    getCurrentTimestampSeconds(),
                    if (exito) 1L else 0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }

    override suspend fun actualizarSubtarea(subtarea: Subtarea) {
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                local.actualizarSubtarea(subtarea)
            } catch (e: Exception) {
                println("Error local al actualizar subtarea: ${e.message}")
            }
        }

        if (SettingsManager.isUsuarioInvitado()) return

        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            try {
                exito = remote.actualizarSubtarea(subtarea)
            } catch (e: Exception) {
                println("Servidor offline al actualizar subtarea: ${e.message}")
            }
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.ACTUALIZACION.getNombreAccion(),
                    Entity.SUBTAREA.getNombreEntity(),
                    subtarea.idSubtarea,
                    SubtareaMapper.toJson(subtarea),
                    getCurrentTimestampSeconds(),
                    if (exito) 1L else 0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }

    override suspend fun eliminarSubtarea(idSubtarea: String) {
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                local.eliminarSubtarea(idSubtarea)
            } catch (e: Exception) {
                println("Error local al eliminar subtarea: ${e.message}")
            }
        }

        if (SettingsManager.isUsuarioInvitado()) return

        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            try {
                exito = remote.eliminarSubtarea(idSubtarea)
            } catch (e: Exception) {
                println("Servidor offline al eliminar subtarea: ${e.message}")
            }
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.ELIMINACION.getNombreAccion(),
                    Entity.SUBTAREA.getNombreEntity(),
                    idSubtarea,
                    "",
                    getCurrentTimestampSeconds(),
                    if (exito) 1L else 0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }
}