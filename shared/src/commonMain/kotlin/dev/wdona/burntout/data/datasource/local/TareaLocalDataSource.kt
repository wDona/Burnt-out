package dev.wdona.burntout.data.datasource.local

import dev.wdona.burntout.data.datasource.common.TareaDataSource
import dev.wdona.burntout.shared.domain.Tarea

interface TareaLocalDataSource : TareaDataSource {
    suspend fun eliminarTareasPorTablero(idTablero: Long)
    override suspend fun insertOrUpdateTarea(tarea: Tarea): Boolean
}