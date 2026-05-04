package dev.wdona.burntout.data.datasource.common

import dev.wdona.burntout.shared.domain.Subtarea

interface SubtareaDataSource {
    suspend fun getSubtareasByTarea(idTarea: String): List<Subtarea>
    suspend fun crearSubtarea(subtarea: Subtarea): String
    suspend fun actualizarSubtarea(subtarea: Subtarea): Boolean
    suspend fun eliminarSubtarea(idSubtarea: String): Boolean
}