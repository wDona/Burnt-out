package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.SubtareaApi
import dev.wdona.burntout.shared.domain.Subtarea
import dev.wdona.burntout.shared.network.ApiClient
import dev.wdona.burntout.shared.utils.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.Serializable

@Serializable
private data class CompletadoRequest(val completado: Boolean)

class SubtareaApiImpl(private val client: HttpClient = ApiClient.client) : SubtareaApi {
    private val TAG = "SubtareaApiImpl"

    override suspend fun getSubtareasByTarea(idTarea: String): List<Subtarea> {
        Logger.d(TAG, "getSubtareasByTarea: $idTarea")
        return client.get("subtareas?idTarea=$idTarea").body()
    }

    override suspend fun crearSubtarea(subtarea: Subtarea): HttpResponse {
        Logger.d(TAG, "crearSubtarea: $subtarea")
        return client.post("subtareas") {
            contentType(ContentType.Application.Json)
            setBody(subtarea)
        }
    }

    override suspend fun actualizarSubtarea(subtarea: Subtarea): HttpResponse {
        Logger.d(TAG, "actualizarSubtarea: $subtarea")
        return client.patch("subtareas/${subtarea.idSubtarea}") {
            contentType(ContentType.Application.Json)
            setBody(CompletadoRequest(subtarea.completado))
        }
    }

    override suspend fun eliminarSubtarea(idSubtarea: String): HttpResponse {
        Logger.d(TAG, "eliminarSubtarea: $idSubtarea")
        return client.delete("subtareas/$idSubtarea")
    }
}