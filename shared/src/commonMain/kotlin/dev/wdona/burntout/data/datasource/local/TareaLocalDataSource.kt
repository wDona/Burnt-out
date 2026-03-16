package dev.wdona.burntout.data.datasource.local

import dev.wdona.burntout.data.datasource.common.TareaDataSource

interface TareaLocalDataSource : TareaDataSource {
    suspend fun eliminarTareasPorTablero(idTablero: Long)
}