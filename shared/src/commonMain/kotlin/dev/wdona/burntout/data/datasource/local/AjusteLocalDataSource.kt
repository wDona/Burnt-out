package dev.wdona.burntout.data.datasource.local

import dev.wdona.burntout.data.datasource.common.AjusteDataSource
import dev.wdona.burntout.domain.model.Ajuste

interface AjusteLocalDataSource : AjusteDataSource {
    suspend fun eliminarAjuste(idAjuste: Long)
    suspend fun anadirAjuste(ajuste: Ajuste)
    suspend fun insertOrUpdateAjuste(ajuste: Ajuste)
    suspend fun getAjusteByNombreYUsuario(nombre: String, idUsuario: Long): Ajuste?
}