package dev.wdona.burntout.data.api

import dev.wdona.burntout.domain.model.Ajuste
import dev.wdona.burntout.domain.model.Respuesta
import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.domain.Organizacion
import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.shared.domain.Subtarea
import dev.wdona.burntout.shared.domain.Tablero
import dev.wdona.burntout.shared.domain.Tarea
import dev.wdona.burntout.shared.domain.Usuario
import kotlinx.serialization.Serializable

@Serializable
data class SyncPullRequest(
    val lastSyncTimestamp: Long,
    val idUsuario: Long,
    val idOrganizacion: Long
)

@Serializable
data class SyncResponse(
    val tareas: List<Tarea>,
    val subtareas: List<Subtarea>,
    val respuestas: List<Respuesta>,
    val preguntas: List<Pregunta>,
    val tableros: List<Tablero>,
    val equipos: List<Equipo>,
    val usuarios: List<Usuario>,
    val organizaciones: List<Organizacion>,
    val ajustes: List<Ajuste>,
    val serverTimestamp: Long
)

interface SyncApi {
    suspend fun pull(request: SyncPullRequest): SyncResponse
}
