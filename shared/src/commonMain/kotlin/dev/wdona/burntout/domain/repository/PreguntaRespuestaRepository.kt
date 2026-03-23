package dev.wdona.burntout.data.dao

import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.domain.model.Respuesta

interface PreguntaRespuestaRepository {
    suspend fun getPreguntasByOrg(idOrg: Long): List<Pregunta>
    suspend fun getRespuestasByPregunta(idPregunta: Long): List<Respuesta>
    suspend fun crearPregunta(pregunta: Pregunta)
    suspend fun actualizarPregunta(pregunta: Pregunta)
    suspend fun eliminarPregunta(idPregunta: Long)
    suspend fun responderPregunta(respuesta: Respuesta)
    suspend fun getRespuestasByIdUsuario(idUser: Long): List<Respuesta>
    suspend fun getLastRespuestasByIdUsuario(idUser: Long): List<Respuesta>
    suspend fun getRespuestasByIdUsuarioAndDate(idUser: Long, date: Long): List<Respuesta>
}