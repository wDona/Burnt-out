package dev.wdona.burntout.data.datasource.local

import dev.wdona.burntout.data.datasource.common.AjusteDataSource
import dev.wdona.burntout.domain.model.Ajuste

interface AjusteLocalDataSource : AjusteDataSource {
    fun eliminarAjuste(idAjuste: Long)
    fun anadirAjuste(ajuste: Ajuste)
}