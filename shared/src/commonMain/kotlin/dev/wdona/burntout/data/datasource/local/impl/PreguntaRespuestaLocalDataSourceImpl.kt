package dev.wdona.burntout.data.datasource.local.impl

import dev.wdona.burntout.data.dao.PreguntaRespuestaDao
import dev.wdona.burntout.data.datasource.local.PreguntaRespuestaLocalDataSource
import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.domain.model.Respuesta

class PreguntaRespuestaLocalDataSourceImpl(private val dao: PreguntaRespuestaDao): PreguntaRespuestaLocalDataSource {
    override suspend fun getPreguntasByOrg(idOrg: Long): List<Pregunta> {
        return dao.getPreguntasByOrg(idOrg)
    }

    override suspend fun crearPregunta(pregunta: Pregunta): Long {
        return dao.crearPregunta(pregunta)
    }

    override suspend fun upsertPregunta(pregunta: Pregunta) {
        dao.upsertPregunta(pregunta)
    }

    override suspend fun actualizarPregunta(pregunta: Pregunta): Boolean {
        return dao.actualizarPregunta(pregunta)
    }

    override suspend fun eliminarPregunta(idPregunta: Long): Boolean {
        return dao.eliminarPregunta(idPregunta)
    }

    override suspend fun responderPregunta(respuesta: Respuesta) {
        dao.responderPregunta(respuesta)
    }

    override suspend fun getRespuestasByPregunta(idPregunta: Long): List<Respuesta> {
        return dao.getRespuestasByPregunta(idPregunta)
    }

    override suspend fun getRespuestasByIdUsuario(idUser: Long): List<Respuesta> {
        return dao.getRespuestasByIdUsuario(idUser)
    }

    override suspend fun getLastRespuestasByIdUsuario(idUser: Long): List<Respuesta> {
        return dao.getLastRespuestasByIdUsuario(idUser)
    }

    override suspend fun getRespuestasByIdUsuarioAndDate(idUser: Long, date: Long): List<Respuesta> {
        return dao.getRespuestasByIdUsuarioAndDate(idUser, date)
    }
}
