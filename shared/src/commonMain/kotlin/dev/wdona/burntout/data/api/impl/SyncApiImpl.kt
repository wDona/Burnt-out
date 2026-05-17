package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.SyncApi
import dev.wdona.burntout.domain.model.Ajuste
import dev.wdona.burntout.domain.model.Respuesta
import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.domain.Organizacion
import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.shared.domain.Subtarea
import dev.wdona.burntout.shared.domain.Tablero
import dev.wdona.burntout.shared.domain.Tarea
import dev.wdona.burntout.shared.domain.Usuario
import dev.wdona.burntout.shared.network.ApiClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

class SyncApiImpl(private val client: HttpClient = ApiClient.client) : SyncApi {
    override suspend fun pull(request: SyncPullRequest): SyncResponse {
        return client.post("sync/pull") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}

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
