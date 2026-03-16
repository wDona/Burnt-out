package dev.wdona.burntout.data.api

import dev.wdona.burntout.domain.model.Ajuste

interface AjusteApi {
    fun anadirAjuste(ajuste: Ajuste)

    fun modificarAjuste(ajuste: Ajuste)

    fun eliminarAjuste(idAjuste: Long)
}