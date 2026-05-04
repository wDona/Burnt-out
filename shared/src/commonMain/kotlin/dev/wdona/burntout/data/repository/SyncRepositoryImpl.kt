package dev.wdona.burntout.data.repository

import dev.wdona.burntout.data.api.SyncApi
import dev.wdona.burntout.data.api.SyncPullRequest
import dev.wdona.burntout.data.datasource.local.*
import dev.wdona.burntout.domain.repository.SyncRepository
import dev.wdona.burntout.domain.usecase.SincronizarPendientesUseCase
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
    private val ajusteLocal: AjusteLocalDataSource
) : SyncRepository {

    override suspend fun sync(): Boolean = withContext(Dispatchers.IO) {
        val pushOk = sincronizarPendientes()
        if (!pushOk) return@withContext false

        try {
            val lastSync = SettingsManager.getLastSyncTimestamp()
            val idUsuario = SettingsManager.getIdUsuarioActual()
            val idOrg = SettingsManager.getIdOrganizacionActual()

            val response = syncApi.pull(SyncPullRequest(lastSync, idUsuario, idOrg))

            response.organizaciones.forEach { organizacionLocal.insertOrUpdateOrganizacion(it) }
            response.usuarios.forEach { usuarioLocal.insertOrUpdateUsuario(it) }
            response.equipos.forEach { equipoLocal.insertOrUpdateEquipo(it) }
            response.tableros.forEach { tableroLocal.insertOrUpdateTablero(it) }
            response.preguntas.forEach { preguntaRespuestaLocal.upsertPregunta(it) }
            response.tareas.forEach { tareaLocal.insertOrUpdateTarea(it) }
            response.subtareas.forEach { subtareaLocal.insertOrUpdateSubtarea(it) }
            // Para respuestas, como son inmutables o se identifican por UUID, insertOrUpdate es seguro
            response.respuestas.forEach { preguntaRespuestaLocal.responderPregunta(it) }
            response.ajustes.forEach { ajusteLocal.insertOrUpdateAjuste(it) }

            SettingsManager.setLastSyncTimestamp(response.serverTimestamp)
            true
        } catch (e: Exception) {
            println("Error en Pull Sync: ${e.message}")
            false
        }
    }
}
