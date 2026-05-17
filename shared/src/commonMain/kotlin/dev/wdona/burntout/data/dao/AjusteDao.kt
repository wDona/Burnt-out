package dev.wdona.burntout.data.dao

import dev.wdona.burntout.domain.model.Ajuste

interface AjusteDao {
    fun anadirAjuste(ajuste: Ajuste)
    fun modificarAjuste(idUsuario: Long, ajuste: Ajuste)
    fun eliminarAjuste(idAjuste: Long)
    fun getAjustesByUsuario(idUsuario: Long): List<Ajuste>
    fun getAjusteByIdYUsuario(idAjuste: Long, idUsuario: Long): Ajuste?
    fun insertOrUpdateAjuste(ajuste: Ajuste)
    fun getAjusteByNombreYUsuario(nombre: String, idUsuario: Long): Ajuste?
}