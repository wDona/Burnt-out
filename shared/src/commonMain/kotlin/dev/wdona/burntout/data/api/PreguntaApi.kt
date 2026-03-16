package dev.wdona.burntout.data.api

import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.shared.domain.Respuesta
import io.ktor.client.statement.HttpResponse

interface PreguntaApi {
    suspend fun getPreguntasByOrg(idOrg: Long): List<Pregunta>
    suspend fun crearPregunta(pregunta: Pregunta): HttpResponse
    suspend fun actualizarPregunta(pregunta: Pregunta): HttpResponse
    suspend fun eliminarPregunta(idPregunta: Long): HttpResponse
    suspend fun responderPregunta(respuesta: Respuesta): HttpResponse
    suspend fun getRespuestasByPregunta(idPregunta: Long): List<Respuesta>
}