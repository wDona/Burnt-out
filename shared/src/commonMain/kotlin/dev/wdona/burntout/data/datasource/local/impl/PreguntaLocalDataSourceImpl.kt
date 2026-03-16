package dev.wdona.burntout.data.datasource.local.impl

import dev.wdona.burntout.data.dao.PreguntaDao
import dev.wdona.burntout.data.datasource.local.PreguntaLocalDataSource
import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.shared.domain.Respuesta

class PreguntaLocalDataSourceImpl(private val dao: PreguntaDao): PreguntaLocalDataSource {
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
}
