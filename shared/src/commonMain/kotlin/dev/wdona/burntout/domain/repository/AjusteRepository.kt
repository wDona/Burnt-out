package dev.wdona.burntout.domain.repository

import dev.wdona.burntout.domain.model.Ajuste

interface AjusteRepository {
    suspend fun getAjustesByUsuario(idUsuario: Long): List<Ajuste>
    suspend fun getAjusteByIdYUsuario(idAjuste: Long, idUsuario: Long): Ajuste
    suspend fun modificarAjuste(idUsuario: Long, ajuste: Ajuste)
    suspend fun salirDelEquipo(idEquipo: Long, idUsuario: Long): Boolean
}