package dev.wdona.burntout.data.dao

import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.shared.domain.Respuesta

interface PreguntaRepository {
    suspend fun getPreguntasByOrg(idOrg: Long): List<Pregunta>
    suspend fun getRespuestasByPregunta(idPregunta: Long): List<Respuesta>
    suspend fun crearPregunta(pregunta: Pregunta)
    suspend fun actualizarPregunta(pregunta: Pregunta)
    suspend fun eliminarPregunta(idPregunta: Long)
    suspend fun responderPregunta(respuesta: Respuesta)
}