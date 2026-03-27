package dev.wdona.burntout.domain.repository

import dev.wdona.burntout.domain.model.Ajuste

interface AjusteRepository {
    suspend fun modificarAjuste(ajuste: Ajuste)
    suspend fun getAjustesByUsuario(idUsuario: Long): List<Ajuste>
    suspend fun getAjusteByIdYUsuario(idAjuste: Long, idUsuario: Long): Ajuste
}