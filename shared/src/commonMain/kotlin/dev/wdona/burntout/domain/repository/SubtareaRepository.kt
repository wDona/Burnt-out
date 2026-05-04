package dev.wdona.burntout.data.dao

import dev.wdona.burntout.shared.domain.Subtarea

interface SubtareaRepository {
    suspend fun getSubtareasByTarea(idTarea: String): List<Subtarea>
    suspend fun crearSubtarea(subtarea: Subtarea)
    suspend fun actualizarSubtarea(subtarea: Subtarea)
    suspend fun eliminarSubtarea(idSubtarea: String)
}