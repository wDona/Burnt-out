package dev.wdona.burntout.data.dao.impl

import dev.wdona.burntout.data.dao.SubtareaDao
import dev.wdona.burntout.data.datasource.mapper.SubtareaMapper
import dev.wdona.burntout.shared.db.AppDatabase
import dev.wdona.burntout.shared.domain.Subtarea
import dev.wdona.burntout.shared.utils.Logger
import dev.wdona.burntout.shared.utils.getCurrentTimestampSeconds

class SubtareaDaoImpl(appDatabase: AppDatabase) : SubtareaDao {
    private val queries = appDatabase.appDatabaseQueries
    private val TAG = "SubtareaDaoImpl"

    override suspend fun getSubtareasByTarea(idTarea: String): List<Subtarea> {
        Logger.d(TAG, "getSubtareasByTarea: $idTarea")
        return queries.getSubtareasByTarea(idTarea).executeAsList().map {
            SubtareaMapper.toDomain(it)
        }
    }

    override suspend fun crearSubtarea(subtarea: Subtarea): String {
        Logger.d(TAG, "crearSubtarea: ${subtarea.titulo}")
        val generatedId = subtarea.idSubtarea.ifEmpty { java.util.UUID.randomUUID().toString() }
        queries.insertSubtarea(
            ID_Subtarea = generatedId,
            Titulo = subtarea.titulo,
            Completado = if (subtarea.completado) 1L else 0L,
            FK_ID_Tarea = subtarea.idTareaPerteneciente,
            Updated_At = subtarea.updatedAt.takeIf { it > 0L } ?: getCurrentTimestampSeconds()
        )
        return generatedId
    }

    override suspend fun insertOrUpdateSubtarea(subtarea: Subtarea): Boolean {
        Logger.d(TAG, "insertOrUpdateSubtarea: ${subtarea.idSubtarea}")
        return try {
            queries.upsertSubtarea(
                ID_Subtarea = subtarea.idSubtarea,
                Titulo = subtarea.titulo,
                Completado = if (subtarea.completado) 1L else 0L,
                FK_ID_Tarea = subtarea.idTareaPerteneciente,
                Is_Deleted = if (subtarea.isDeleted) 1L else 0L,
                Updated_At = subtarea.updatedAt.takeIf { it > 0L } ?: getCurrentTimestampSeconds()
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
                Updated_At = subtarea.updatedAt.takeIf { it > 0L } ?: getCurrentTimestampSeconds(),
                ID_Subtarea = subtarea.idSubtarea
            )
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error actualizarSubtarea: ${e.message}")
            false
        }
    }

    override suspend fun eliminarSubtarea(idSubtarea: String): Boolean {
        Logger.d(TAG, "eliminarSubtarea: $idSubtarea")
        return try {
            queries.deleteSubtarea(
                Updated_At = getCurrentTimestampSeconds(),
                ID_Subtarea = idSubtarea
            )
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error eliminarSubtarea: ${e.message}")
            false
        }
    }
}