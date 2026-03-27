package dev.wdona.burntout.data.datasource.local.impl

import dev.wdona.burntout.data.dao.SubtareaDao
import dev.wdona.burntout.data.datasource.local.SubtareaLocalDataSource
import dev.wdona.burntout.shared.domain.Subtarea

class SubtareaLocalDataSourceImpl(private val dao: SubtareaDao) : SubtareaLocalDataSource {

    override suspend fun getSubtareasByTarea(idTarea: Long): List<Subtarea> =
        dao.getSubtareasByTarea(idTarea)

    override suspend fun crearSubtarea(subtarea: Subtarea): Long =
        dao.crearSubtarea(subtarea)

    override suspend fun insertOrUpdateSubtarea(subtarea: Subtarea): Boolean =
        dao.insertOrUpdateSubtarea(subtarea)

    override suspend fun actualizarSubtarea(subtarea: Subtarea): Boolean =
        dao.actualizarSubtarea(subtarea)

    override suspend fun eliminarSubtarea(idSubtarea: Long): Boolean =
        dao.eliminarSubtarea(idSubtarea)
}