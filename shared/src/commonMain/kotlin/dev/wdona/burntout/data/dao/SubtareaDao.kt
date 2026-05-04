package dev.wdona.burntout.data.dao

import dev.wdona.burntout.shared.domain.Subtarea

interface SubtareaDao {
    suspend fun getSubtareasByTarea(idTarea: String): List<Subtarea>
    suspend fun crearSubtarea(subtarea: Subtarea): String
    suspend fun insertOrUpdateSubtarea(subtarea: Subtarea): Boolean
    suspend fun actualizarSubtarea(subtarea: Subtarea): Boolean
    suspend fun eliminarSubtarea(idSubtarea: String): Boolean
}