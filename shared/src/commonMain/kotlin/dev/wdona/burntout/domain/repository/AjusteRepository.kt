package dev.wdona.burntout.domain.repository

import dev.wdona.burntout.domain.model.Ajuste

interface AjusteRepository {
    fun modificarAjuste(ajuste: Ajuste)
    fun getAjustesByUsuario(idUsuario: Long): List<Ajuste>
    fun getAjusteByIdYUsuario(idAjuste: Long, idUsuario: Long): Ajuste
}