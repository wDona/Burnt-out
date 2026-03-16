package dev.wdona.burntout.data.dao.impl

import dev.wdona.burntout.data.dao.TareaDao
import dev.wdona.burntout.data.datasource.mapper.TareaMapper
import dev.wdona.burntout.shared.domain.Tarea
import dev.wdona.burntout.shared.db.AppDatabase

class TareaDaoImpl(appDatabase: AppDatabase) : TareaDao {
    private val queries = appDatabase.appDatabaseQueries

    override suspend fun getTareasByTablero(idTablero: Long): List<Tarea> {
        return TareaMapper.toDomainList(
            queries.getTareasByTablero(idTablero)
        )
    }

    override suspend fun getTareaById(idTarea: Long): Tarea {
        return TareaMapper.toDomain(
            queries.getTareaById(idTarea).executeAsOne()
        )
    }

    override suspend fun crearTarea(tarea: Tarea) : Long {
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
        try {
            queries.updateTareaById(
                ID_Tarea = tarea.idTarea,
                Titulo = tarea.titulo,
                Descripcion = tarea.descripcion,
                Estado = tarea.estado
            )
        } catch (e: Exception) {
            return false
        }
        return true
    }

    override suspend fun eliminarTarea(tareaId: Long) : Boolean {
        try {
            queries.deleteTareaById(tareaId)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    override suspend fun insertOrUpdateTarea(tarea: Tarea) : Boolean {
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
            return false
        }
        return true
    }

    override suspend fun eliminarTareasByTableroId(tableroId: Long) {
        queries.deleteTareasByTablero(tableroId)
    }
}
