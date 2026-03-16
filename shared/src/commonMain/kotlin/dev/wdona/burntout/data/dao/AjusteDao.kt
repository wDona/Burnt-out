package dev.wdona.burntout.data.dao

import dev.wdona.burntout.domain.model.Ajuste

interface AjusteDao {
    fun anadirAjuste(ajuste: Ajuste)
    fun modificarAjuste(ajuste: Ajuste)
    fun eliminarAjuste(idAjuste: Long)
    fun getAjustesByUsuario(idUsuario: Long): List<Ajuste>
    fun getAjusteByIdYUsuario(idAjuste: Long, idUsuario: Long): Ajuste
}