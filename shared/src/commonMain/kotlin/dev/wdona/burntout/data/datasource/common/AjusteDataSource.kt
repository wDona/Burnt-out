package dev.wdona.burntout.data.datasource.common

import dev.wdona.burntout.domain.model.Ajuste

interface AjusteDataSource {
    fun modificarAjuste(ajuste: Ajuste)
    fun getAjustesByUsuario(idUsuario: Long): List<Ajuste>
    fun getAjusteByIdYUsuario(idAjuste: Long, idUsuario: Long): Ajuste
}