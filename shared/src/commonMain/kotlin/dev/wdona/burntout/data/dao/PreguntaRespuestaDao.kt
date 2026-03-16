package dev.wdona.burntout.data.dao

import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.shared.domain.Respuesta

interface PreguntaRespuestaDao {
    suspend fun getPreguntasByOrg(idOrg: Long): List<Pregunta>
    suspend fun crearPregunta(pregunta: Pregunta): Long
    suspend fun upsertPregunta(pregunta: Pregunta)
    suspend fun actualizarPregunta(pregunta: Pregunta): Boolean
    suspend fun eliminarPregunta(idPregunta: Long): Boolean
    
    suspend fun responderPregunta(respuesta: Respuesta)
    suspend fun getRespuestasByPregunta(idPregunta: Long): List<Respuesta>
}