package dev.wdona.burntout.data.datasource.local

import dev.wdona.burntout.data.datasource.common.SubtareaDataSource
import dev.wdona.burntout.shared.domain.Subtarea

interface SubtareaLocalDataSource : SubtareaDataSource {
    suspend fun insertOrUpdateSubtarea(subtarea: Subtarea): Boolean
}