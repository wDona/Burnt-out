package dev.wdona.burntout.data.api

import dev.wdona.burntout.domain.model.Ajuste

interface AjusteApi {
    suspend fun getAjustes(idUsuario: Long): List<Ajuste>
    suspend fun anadirAjuste(idUsuario: Long, ajuste: Ajuste): Ajuste
    suspend fun modificarAjuste(idUsuario: Long, ajuste: Ajuste): Ajuste
    suspend fun eliminarAjuste(idUsuario: Long, idAjuste: Long)
}