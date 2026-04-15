package dev.wdona.burntout.data.dao.impl

import dev.wdona.burntout.data.dao.TareaDao
import dev.wdona.burntout.data.datasource.mapper.TareaMapper
import dev.wdona.burntout.shared.domain.Tarea
import dev.wdona.burntout.shared.db.AppDatabase
import dev.wdona.burntout.shared.utils.Logger

class TareaDaoImpl(appDatabase: AppDatabase) : TareaDao {
    private val queries = appDatabase.appDatabaseQueries
    private val TAG = "TareaDaoImpl"

    override suspend fun getTareasByTablero(idTablero: Long): List<Tarea> {
        Logger.d(TAG, "getTareasByTablero: $idTablero")
        return TareaMapper.toDomainList(
            queries.getTareasByTablero(idTablero)
        )
    }

    override suspend fun getTareaById(idTarea: Long): Tarea? {
        Logger.d(TAG, "getTareaById: $idTarea")
        val entity = queries.getTareaById(idTarea).executeAsOneOrNull()
        return entity?.let { TareaMapper.toDomain(it) }
    }

    override suspend fun crearTarea(tarea: Tarea) : Long {
        Logger.d(TAG, "crearTarea: $tarea")
        queries.insertTarea(
            Titulo = tarea.titulo,
            Descripcion = tarea.descripcion,
            Estado = tarea.estado,
            FK_ID_Tabl = tarea.idTableroPerteneciente,
            FK_ID_Usuario = tarea.idUsuarioAsignado
        )
        return queries.lastInsertRowId().executeAsOne()
    }

    override suspend fun actualizarTarea(tarea: Tarea) : Boolean {
        Logger.d(TAG, "actualizarTarea: $tarea")
        try {
            queries.updateTareaById(
                ID_Tarea = tarea.idTarea,
                Titulo = tarea.titulo,
                Descripcion = tarea.descripcion,
                Estado = tarea.estado
            )
        } catch (e: Exception) {
            Logger.d(TAG, "Error actualizarTarea: ${e.message}")
            return false
        }
        return true
    }

    override suspend fun eliminarTarea(tareaId: Long) : Boolean {
        Logger.d(TAG, "eliminarTarea: $tareaId")
        try {
            queries.deleteTareaById(tareaId)
        } catch (e: Exception) {
            Logger.d(TAG, "Error eliminarTarea: ${e.message}")
            return false
        }
        return true
    }

    override suspend fun insertOrUpdateTarea(tarea: Tarea) : Boolean {
        Logger.d(TAG, "insertOrUpdateTarea: $tarea")
        try {
            // Esta se usa para sincronización, aquí SÍ pasamos el ID
            queries.upsertTarea(
                ID_Tarea = tarea.idTarea,
                Titulo = tarea.titulo,
                Descripcion = tarea.descripcion,
                Estado = tarea.estado,
                FK_ID_Tabl = tarea.idTableroPerteneciente,
                FK_ID_Usuario = tarea.idUsuarioAsignado
            )
        } catch (e: Exception) {
            Logger.d(TAG, "Error insertOrUpdateTarea: ${e.message}")
            return false
        }
        return true
    }

    override suspend fun eliminarTareasByTableroId(tableroId: Long) {
        Logger.d(TAG, "eliminarTareasByTableroId: $tableroId")
        queries.deleteTareasByTablero(tableroId)
    }
}
