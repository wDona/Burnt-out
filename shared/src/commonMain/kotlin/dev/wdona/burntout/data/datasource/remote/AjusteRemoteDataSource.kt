package dev.wdona.burntout.data.datasource.remote

import dev.wdona.burntout.data.datasource.common.AjusteDataSource
import dev.wdona.burntout.domain.model.Ajuste

interface AjusteRemoteDataSource : AjusteDataSource {
    suspend fun anadirAjuste(idUsuario: Long, ajuste: Ajuste): Ajuste
}