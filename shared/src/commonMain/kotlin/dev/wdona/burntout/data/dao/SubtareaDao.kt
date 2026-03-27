package dev.wdona.burntout.data.dao

import dev.wdona.burntout.shared.domain.Subtarea

interface SubtareaDao {
    suspend fun getSubtareasByTarea(idTarea: Long): List<Subtarea>
    suspend fun crearSubtarea(subtarea: Subtarea): Long
    suspend fun insertOrUpdateSubtarea(subtarea: Subtarea): Boolean
    suspend fun actualizarSubtarea(subtarea: Subtarea): Boolean
    suspend fun eliminarSubtarea(idSubtarea: Long): Boolean
}