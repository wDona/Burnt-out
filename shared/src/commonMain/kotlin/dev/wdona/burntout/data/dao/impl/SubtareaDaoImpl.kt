package dev.wdona.burntout.data.dao.impl

import dev.wdona.burntout.data.dao.SubtareaDao
import dev.wdona.burntout.data.datasource.mapper.SubtareaMapper
import dev.wdona.burntout.shared.db.AppDatabase
import dev.wdona.burntout.shared.domain.Subtarea
import dev.wdona.burntout.shared.utils.Logger

class SubtareaDaoImpl(appDatabase: AppDatabase) : SubtareaDao {
    private val queries = appDatabase.appDatabaseQueries
    private val TAG = "SubtareaDaoImpl"

    override suspend fun getSubtareasByTarea(idTarea: Long): List<Subtarea> {
        Logger.d(TAG, "getSubtareasByTarea: $idTarea")
        return queries.getSubtareasByTarea(idTarea).executeAsList().map {
            SubtareaMapper.toDomain(it)
        }
    }

    override suspend fun crearSubtarea(subtarea: Subtarea): Long {
        Logger.d(TAG, "crearSubtarea: ${subtarea.titulo}")
        queries.insertSubtarea(
            Titulo = subtarea.titulo,
            Completado = if (subtarea.completado) 1L else 0L,
            FK_ID_Tarea = subtarea.idTareaPerteneciente
        )
        return queries.lastInsertRowId().executeAsOne()
    }

    override suspend fun insertOrUpdateSubtarea(subtarea: Subtarea): Boolean {
        Logger.d(TAG, "insertOrUpdateSubtarea: ${subtarea.idSubtarea}")
        return try {
            queries.upsertSubtarea(
                ID_Subtarea = subtarea.idSubtarea,
                Titulo = subtarea.titulo,
                Completado = if (subtarea.completado) 1L else 0L,
                FK_ID_Tarea = subtarea.idTareaPerteneciente
            )
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error insertOrUpdateSubtarea: ${e.message}")
            false
        }
    }

    override suspend fun actualizarSubtarea(subtarea: Subtarea): Boolean {
        Logger.d(TAG, "actualizarSubtarea: ${subtarea.idSubtarea}")
        return try {
            queries.updateSubtarea(
                Titulo = subtarea.titulo,
                Completado = if (subtarea.completado) 1L else 0L,
                ID_Subtarea = subtarea.idSubtarea
            )
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error actualizarSubtarea: ${e.message}")
            false
        }
    }

    override suspend fun eliminarSubtarea(idSubtarea: Long): Boolean {
        Logger.d(TAG, "eliminarSubtarea: $idSubtarea")
        return try {
            queries.deleteSubtarea(idSubtarea)
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error eliminarSubtarea: ${e.message}")
            false
        }
    }
}