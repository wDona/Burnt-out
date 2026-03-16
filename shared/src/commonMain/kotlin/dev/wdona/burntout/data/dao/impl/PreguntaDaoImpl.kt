package dev.wdona.burntout.data.dao.impl

import dev.wdona.burntout.data.dao.PreguntaDao
import dev.wdona.burntout.data.datasource.mapper.PreguntaMapper
import dev.wdona.burntout.data.datasource.mapper.RespuestaMapper
import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.shared.domain.Respuesta
import dev.wdona.burntout.shared.db.AppDatabase

class PreguntaDaoImpl(appDatabase: AppDatabase) : PreguntaDao {
    private val queries = appDatabase.appDatabaseQueries

    override suspend fun getPreguntasByOrg(idOrg: Long): List<Pregunta> {
        return PreguntaMapper.toDomainList(
            queries.getPreguntasByOrg(idOrg).executeAsList()
        )
    }

    override suspend fun crearPregunta(pregunta: Pregunta): Long {
        queries.insertPregunta(
            Pregunta = pregunta.pregunta,
            FK_ID_Org = pregunta.idOrganizacion
        )
        return queries.lastInsertRowId().executeAsOne()
    }

    override suspend fun upsertPregunta(pregunta: Pregunta) {
        queries.upsertPregunta(
            ID_Pregunta = pregunta.idPregunta,
            Pregunta = pregunta.pregunta,
            FK_ID_Org = pregunta.idOrganizacion
        )
    }

    override suspend fun actualizarPregunta(pregunta: Pregunta): Boolean {
        queries.updatePregunta(
            Pregunta = pregunta.pregunta,
            ID_Pregunta = pregunta.idPregunta
        )
        return true
    }

    override suspend fun eliminarPregunta(idPregunta: Long): Boolean {
        queries.deletePregunta(idPregunta)
        return true
    }

    override suspend fun responderPregunta(respuesta: Respuesta) {
        queries.insertRespuesta(
            ID_Usuario = respuesta.idUsuario,
            ID_Pregunta = respuesta.idPregunta,
            Anonimo = if (respuesta.anonimo) 1L else 0L,
            Respuesta = respuesta.respuesta
        )
    }

    override suspend fun getRespuestasByPregunta(idPregunta: Long): List<Respuesta> {
        val rows = queries.getRespuestasByPregunta(idPregunta).executeAsList()
        // Note: The new query returns a specific projection matching mapped fields
        return rows.map { row ->
            Respuesta(
                idUsuario = row.ID_Usuario,
                idPregunta = row.ID_Pregunta,
                anonimo = row.Anonimo == 1L,
                respuesta = row.Respuesta,
                nombreUsuario = row.Nombre
            )
        }
    }
}
