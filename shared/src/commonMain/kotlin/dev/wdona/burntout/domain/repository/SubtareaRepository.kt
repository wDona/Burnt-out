package dev.wdona.burntout.data.dao

import dev.wdona.burntout.shared.domain.Subtarea

interface SubtareaRepository {
    suspend fun getSubtareasByTarea(idTarea: Long): List<Subtarea>
    suspend fun crearSubtarea(subtarea: Subtarea)
    suspend fun actualizarSubtarea(subtarea: Subtarea)
    suspend fun eliminarSubtarea(idSubtarea: Long)
}