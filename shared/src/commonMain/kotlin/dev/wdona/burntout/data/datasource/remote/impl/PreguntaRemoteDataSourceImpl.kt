package dev.wdona.burntout.data.datasource.remote.impl

import dev.wdona.burntout.data.api.PreguntaApi
import dev.wdona.burntout.data.datasource.remote.PreguntaRemoteDataSource
import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.shared.domain.Respuesta

class PreguntaRemoteDataSourceImpl(private val api: PreguntaApi): PreguntaRemoteDataSource {
    override suspend fun getPreguntasByOrg(idOrg: Long): List<Pregunta> {
        return api.getPreguntasByOrg(idOrg)
    }

    override suspend fun crearPregunta(pregunta: Pregunta): Long {
        val response = api.crearPregunta(pregunta)
        return if (response.status.value in 200..299) pregunta.idPregunta else -1L
    }

    override suspend fun upsertPregunta(pregunta: Pregunta) {
        // FIXME
        actualizarPregunta(pregunta)
    }

    override suspend fun actualizarPregunta(pregunta: Pregunta): Boolean {
        val response = api.actualizarPregunta(pregunta)
        return response.status.value in 200..299
    }

    override suspend fun eliminarPregunta(idPregunta: Long): Boolean {
        val response = api.eliminarPregunta(idPregunta)
        return response.status.value in 200..299
    }

    override suspend fun responderPregunta(respuesta: Respuesta) {
        api.responderPregunta(respuesta)
    }

    override suspend fun getRespuestasByPregunta(idPregunta: Long): List<Respuesta> {
        return api.getRespuestasByPregunta(idPregunta)
    }
}
