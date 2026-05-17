package dev.wdona.burntout.data.repository

import dev.wdona.burntout.data.api.SyncApi
import dev.wdona.burntout.data.api.SyncPullRequest
import dev.wdona.burntout.data.datasource.local.*
import dev.wdona.burntout.domain.repository.SyncRepository
import dev.wdona.burntout.domain.usecase.SincronizarPendientesUseCase
import dev.wdona.burntout.shared.utils.Logger
import dev.wdona.burntout.shared.utils.SettingsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncRepositoryImpl(
    private val syncApi: SyncApi,
    private val sincronizarPendientes: SincronizarPendientesUseCase,
    private val tareaLocal: TareaLocalDataSource,
    private val subtareaLocal: SubtareaLocalDataSource,
    private val tableroLocal: TableroLocalDataSource,
    private val equipoLocal: EquipoLocalDataSource,
    private val usuarioLocal: UsuarioLocalDataSource,
    private val organizacionLocal: OrganizacionLocalDataSource,
    private val preguntaRespuestaLocal: PreguntaRespuestaLocalDataSource,
    private val ajusteLocal: AjusteLocalDataSource,
    private val onTareasSincronizadas: ((List<dev.wdona.burntout.shared.domain.Tarea>) -> Unit)? = null
) : SyncRepository {

    override suspend fun sync(): Boolean = withContext(Dispatchers.IO) {
        val pushOk = sincronizarPendientes()
        Logger.d("BurntOut Pendientes", "Sincronización de pendientes (push): $pushOk")
        if (!pushOk) return@withContext false

        try {
            val lastSync = SettingsManager.getLastSyncTimestamp()
            val idUsuario = SettingsManager.getIdUsuarioActual()
            val idOrg = SettingsManager.getIdOrganizacionActual()

            Logger.d("BurntOut Sync", "Iniciando Pull Sync")
            val response = syncApi.pull(SyncPullRequest(lastSync, idUsuario, idOrg))

            response.organizaciones.forEach { organizacionLocal.insertOrUpdateOrganizacion(it) }
            response.usuarios.forEach { usuarioLocal.insertOrUpdateUsuario(it) }
            response.equipos.forEach { equipoLocal.insertOrUpdateEquipo(it) }
            response.tableros.forEach { tableroLocal.insertOrUpdateTablero(it) }
            response.preguntas.forEach { preguntaRespuestaLocal.upsertPregunta(it) }
            response.tareas.forEach { tareaLocal.insertOrUpdateTarea(it) }
            response.subtareas.forEach { subtareaLocal.insertOrUpdateSubtarea(it) }
            val tareasConFecha = response.tareas.filter { it.fechaVencimiento != null && !it.isDeleted && it.idUsuarioAsignado == idUsuario }
            if (tareasConFecha.isNotEmpty() && SettingsManager.isNotificacionesActivas()) {
                onTareasSincronizadas?.invoke(tareasConFecha)
            }
            // Para respuestas, como son inmutables o se identifican por UUID, insertOrUpdate es seguro
            response.respuestas.forEach { preguntaRespuestaLocal.responderPregunta(it) }
            response.ajustes.forEach { ajuste ->
                ajusteLocal.insertOrUpdateAjuste(ajuste)
                if (!ajuste.isDeleted) {
                    when (ajuste.nombre) {
                        "respuestas_anonimas" -> SettingsManager.setRespuestasAnonimas(ajuste.valorAjuste == "true")
                        "notificaciones_activas" -> SettingsManager.setNotificacionesActivas(ajuste.valorAjuste == "true")
                    }
                }
            }

            Logger.d("BurntOut Sync", "Pull Sync completado")

            SettingsManager.setLastSyncTimestamp(response.serverTimestamp)
            true
        } catch (e: Exception) {
            println("Error en Pull Sync: ${e.message}")
            false
        }
    }
}
