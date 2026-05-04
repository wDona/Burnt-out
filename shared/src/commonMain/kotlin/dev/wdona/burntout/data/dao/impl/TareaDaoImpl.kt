package dev.wdona.burntout.data.dao.impl

import dev.wdona.burntout.data.dao.TareaDao
import dev.wdona.burntout.data.datasource.mapper.TareaMapper
import dev.wdona.burntout.shared.domain.Tarea
import dev.wdona.burntout.shared.db.AppDatabase
import dev.wdona.burntout.shared.utils.Logger
import dev.wdona.burntout.shared.utils.getCurrentTimestampSeconds

class TareaDaoImpl(appDatabase: AppDatabase) : TareaDao {
    private val queries = appDatabase.appDatabaseQueries
    private val TAG = "TareaDaoImpl"

    override suspend fun getTareasByTablero(idTablero: String): List<Tarea> {
        Logger.d(TAG, "getTareasByTablero: $idTablero")
        return TareaMapper.toDomainList(
            queries.getTareasByTablero(idTablero)
        )
    }

    override suspend fun getTareaById(idTarea: String): Tarea? {
        Logger.d(TAG, "getTareaById: $idTarea")
        val entity = queries.getTareaById(idTarea).executeAsOneOrNull()
        return entity?.let { TareaMapper.toDomain(it) }
    }

    override suspend fun crearTarea(tarea: Tarea) : String {
        Logger.d(TAG, "crearTarea: $tarea")
        val generatedId = tarea.idTarea.ifEmpty { java.util.UUID.randomUUID().toString() }
        queries.insertTarea(
            ID_Tarea = generatedId,
            Titulo = tarea.titulo,
            Descripcion = tarea.descripcion,
            Estado = tarea.estado,
            FK_ID_Tabl = tarea.idTableroPerteneciente,
            FK_ID_Usuario = tarea.idUsuarioAsignado,
            Fecha_Vencimiento = tarea.fechaVencimiento,
            Updated_At = tarea.updatedAt.takeIf { it > 0L } ?: getCurrentTimestampSeconds()
        )
        return generatedId
    }

    override suspend fun actualizarTarea(tarea: Tarea) : Boolean {
        Logger.d(TAG, "actualizarTarea: $tarea")
        try {
            queries.updateTareaById(
                Titulo = tarea.titulo,
                Descripcion = tarea.descripcion,
                Estado = tarea.estado,
                Fecha_Vencimiento = tarea.fechaVencimiento,
                Updated_At = tarea.updatedAt.takeIf { it > 0L } ?: getCurrentTimestampSeconds(),
                ID_Tarea = tarea.idTarea
            )
            queries.assignTareaToUsuario(
                FK_ID_Usuario = tarea.idUsuarioAsignado,
                Updated_At = tarea.updatedAt.takeIf { it > 0L } ?: getCurrentTimestampSeconds(),
                ID_Tarea = tarea.idTarea
            )
        } catch (e: Exception) {
            Logger.d(TAG, "Error actualizarTarea: ${e.message}")
            return false
        }
        return true
    }

    override suspend fun eliminarTarea(tareaId: String) : Boolean {
        Logger.d(TAG, "eliminarTarea: $tareaId")
        try {
            queries.deleteTareaById(
                Updated_At = getCurrentTimestampSeconds(),
                ID_Tarea = tareaId
            )
        } catch (e: Exception) {
            Logger.d(TAG, "Error eliminarTarea: ${e.message}")
            return false
        }
        return true
    }

    override suspend fun insertOrUpdateTarea(tarea: Tarea) : Boolean {
        Logger.d(TAG, "insertOrUpdateTarea: $tarea")
        try {
            queries.upsertTarea(
                ID_Tarea = tarea.idTarea,
                Titulo = tarea.titulo,
                Descripcion = tarea.descripcion,
                Estado = tarea.estado,
                FK_ID_Tabl = tarea.idTableroPerteneciente,
                FK_ID_Usuario = tarea.idUsuarioAsignado,
                Fecha_Vencimiento = tarea.fechaVencimiento,
                Is_Deleted = if (tarea.isDeleted) 1L else 0L,
                Updated_At = tarea.updatedAt.takeIf { it > 0L } ?: getCurrentTimestampSeconds()
            )
        } catch (e: Exception) {
            Logger.d(TAG, "Error insertOrUpdateTarea: ${e.message}")
            return false
        }
        return true
    }

    override suspend fun eliminarTareasByTableroId(tableroId: String) {
        Logger.d(TAG, "eliminarTareasByTableroId: $tableroId")
        queries.deleteTareasByTablero(
            Updated_At = getCurrentTimestampSeconds(),
            FK_ID_Tabl = tableroId
        )
    }
}
