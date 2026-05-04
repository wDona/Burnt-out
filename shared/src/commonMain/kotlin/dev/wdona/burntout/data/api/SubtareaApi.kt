package dev.wdona.burntout.data.api

import dev.wdona.burntout.shared.domain.Subtarea
import io.ktor.client.statement.HttpResponse

interface SubtareaApi {
    suspend fun getSubtareasByTarea(idTarea: String): List<Subtarea>
    suspend fun crearSubtarea(subtarea: Subtarea): HttpResponse
    suspend fun actualizarSubtarea(subtarea: Subtarea): HttpResponse
    suspend fun eliminarSubtarea(idSubtarea: String): HttpResponse
}