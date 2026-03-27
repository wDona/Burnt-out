package dev.wdona.burntout.data.datasource.common

import dev.wdona.burntout.domain.model.Ajuste

interface AjusteDataSource {
    suspend fun modificarAjuste(ajuste: Ajuste)
    suspend fun getAjustesByUsuario(idUsuario: Long): List<Ajuste>
    suspend fun getAjusteByIdYUsuario(idAjuste: Long, idUsuario: Long): Ajuste
}