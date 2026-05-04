package dev.wdona.burntout.data.datasource.local.impl

import dev.wdona.burntout.data.dao.TareaDao
import dev.wdona.burntout.data.datasource.local.TareaLocalDataSource
import dev.wdona.burntout.shared.domain.Tarea

class TareaLocalDataSourceImpl(private val dao: TareaDao) : TareaLocalDataSource {

    override suspend fun getTareasByTablero(idTablero: String): List<Tarea> =
        dao.getTareasByTablero(idTablero)

    override suspend fun getTareaById(
        idTarea: String,
        idTablero: String
    ): Tarea? = dao.getTareaById(idTarea)

    override suspend fun crearTarea(tarea: Tarea) : String =
        dao.crearTarea(tarea)

    override suspend fun actualizarTarea(tarea: Tarea) =
        dao.actualizarTarea(tarea)

    override suspend fun eliminarTarea(tareaId: String) =
        dao.eliminarTarea(tareaId)

    override suspend fun eliminarTareasPorTablero(idTablero: String) {
        dao.eliminarTareasByTableroId(idTablero)
    }

    override suspend fun insertOrUpdateTarea(tarea: Tarea): Boolean {
        return dao.insertOrUpdateTarea(tarea)
    }
}
