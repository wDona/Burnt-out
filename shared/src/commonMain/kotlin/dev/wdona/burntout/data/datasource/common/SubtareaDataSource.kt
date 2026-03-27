package dev.wdona.burntout.data.datasource.common

import dev.wdona.burntout.shared.domain.Subtarea

interface SubtareaDataSource {
    suspend fun getSubtareasByTarea(idTarea: Long): List<Subtarea>
    suspend fun crearSubtarea(subtarea: Subtarea): Long
    suspend fun actualizarSubtarea(subtarea: Subtarea): Boolean
    suspend fun eliminarSubtarea(idSubtarea: Long): Boolean
}