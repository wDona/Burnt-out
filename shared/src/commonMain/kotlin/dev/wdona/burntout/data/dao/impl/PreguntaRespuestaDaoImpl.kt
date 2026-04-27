package dev.wdona.burntout.data.dao.impl

import dev.wdona.burntout.data.dao.PreguntaRespuestaDao
import dev.wdona.burntout.data.datasource.mapper.PreguntaMapper
import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.domain.model.Respuesta
import dev.wdona.burntout.shared.db.AppDatabase
import dev.wdona.burntout.shared.utils.Logger

class PreguntaRespuestaDaoImpl(appDatabase: AppDatabase) : PreguntaRespuestaDao {
    private val queries = appDatabase.appDatabaseQueries
    private val TAG = "PreguntaRespuestaDaoImpl"

    override suspend fun getPreguntasByOrg(idOrg: Long): List<Pregunta> {
        Logger.d(TAG, "getPreguntasByOrg: $idOrg")
        return PreguntaMapper.toDomainList(
            queries.getPreguntasByOrg(idOrg).executeAsList()
        )
    }

    override suspend fun crearPregunta(pregunta: Pregunta): Long {
        Logger.d(TAG, "crearPregunta: $pregunta")
        queries.insertPregunta(
            Pregunta = pregunta.pregunta,
            Categoria = pregunta.categoria,
            FK_ID_Org = pregunta.idOrganizacion
        )
        return queries.lastInsertRowId().executeAsOne()
    }

    override suspend fun upsertPregunta(pregunta: Pregunta) {
        Logger.d(TAG, "upsertPregunta: $pregunta")
        queries.upsertPregunta(
            ID_Pregunta = pregunta.idPregunta,
            Pregunta = pregunta.pregunta,
            Categoria = pregunta.categoria,
            FK_ID_Org = pregunta.idOrganizacion,
            Is_Deleted = if (pregunta.isDeleted) 1L else 0L
        )
    }

    override suspend fun actualizarPregunta(pregunta: Pregunta): Boolean {
        Logger.d(TAG, "actualizarPregunta: $pregunta")
        queries.updatePregunta(
            Pregunta = pregunta.pregunta,
            Categoria = pregunta.categoria,
            ID_Pregunta = pregunta.idPregunta
        )
        return true
    }

    override suspend fun eliminarPregunta(idPregunta: Long): Boolean {
        Logger.d(TAG, "eliminarPregunta: $idPregunta")
        queries.deletePregunta(idPregunta)
        return true
    }

    override suspend fun responderPregunta(respuesta: Respuesta) {
        Logger.d(TAG, "responderPregunta: $respuesta")
        val fecha = respuesta.fecha ?: (System.currentTimeMillis() / 1000L)
        
        queries.transaction {
            queries.deleteRespuestaByDate(
                respuesta.idUsuario,
                respuesta.idPregunta,
                "" + fecha
            )
            
            queries.insertRespuesta(
                ID_Usuario = respuesta.idUsuario,
                ID_Pregunta = respuesta.idPregunta,
                Anonimo = if (respuesta.anonimo) 1L else 0L,
                Respuesta = respuesta.respuesta,
                Fecha = fecha
            )
        }
    }

    override suspend fun getRespuestasByPregunta(idPregunta: Long): List<Respuesta> {
        Logger.d(TAG, "getRespuestasByPregunta: $idPregunta")
        val filas = queries.getRespuestasByPregunta(idPregunta).executeAsList()
        return filas.map { row ->
            Respuesta(
                idUsuario = row.ID_Usuario,
                idPregunta = row.ID_Pregunta,
                anonimo = row.Anonimo == 1L,
                respuesta = row.Respuesta,
                nombreUsuario = row.Nombre
            )
        }
    }

    override suspend fun getRespuestasByIdUsuario(idUsuario: Long): List<Respuesta> {
        Logger.d(TAG, "getRespuestasByIdUsuario: $idUsuario")
        val filas = queries.getRespuestasByUser(idUsuario).executeAsList()
        return filas.map { row ->
            Respuesta(
                idUsuario = row.ID_Usuario,
                idPregunta = row.ID_Pregunta,
                anonimo = row.Anonimo == 1L,
                respuesta = row.Respuesta,
                nombreUsuario = null,
                fecha = row.Fecha
            )
        }
    }

    override suspend fun getLastRespuestasByIdUsuario(idUsuario: Long): List<Respuesta> {
        Logger.d(TAG, "getLastRespuestasByIdUsuario: $idUsuario")
        val filas = queries.getLastRespuestasByUser(idUsuario, idUsuario).executeAsList()
        return filas.map { row ->
            Respuesta(
                idUsuario = row.ID_Usuario,
                idPregunta = row.ID_Pregunta,
                anonimo = row.Anonimo == 1L,
                respuesta = row.Respuesta,
                nombreUsuario = null,
                fecha = row.Fecha
            )
        }
    }

    override suspend fun getRespuestasByIdUsuarioAndDate(idUsuario: Long, date: Long): List<Respuesta> {
        Logger.d(TAG, "getRespuestasByIdUsuarioAndDate: user=$idUsuario, date=$date")
        val filas = queries.getRespuestasByUserAndDate(idUsuario, "" + date).executeAsList()
        return filas.map { row ->
            Respuesta(
                idUsuario = row.ID_Usuario,
                idPregunta = row.ID_Pregunta,
                anonimo = row.Anonimo == 1L,
                respuesta = row.Respuesta,
                nombreUsuario = null,
                fecha = row.Fecha
            )
        }
    }
}