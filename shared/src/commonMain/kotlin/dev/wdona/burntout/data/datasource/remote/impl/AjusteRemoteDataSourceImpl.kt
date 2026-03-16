package dev.wdona.burntout.data.datasource.remote.impl

import dev.wdona.burntout.data.api.AjusteApi
import dev.wdona.burntout.data.datasource.remote.AjusteRemoteDataSource
import dev.wdona.burntout.domain.model.Ajuste

class AjusteRemoteDataSourceImpl(private val api: AjusteApi) : AjusteRemoteDataSource {
    override fun modificarAjuste(ajuste: Ajuste) {
        TODO("Not yet implemented")
    }

    override fun getAjustesByUsuario(idUsuario: Long): List<Ajuste> {
        TODO("Not yet implemented")
    }

    override fun getAjusteByIdYUsuario(
        idAjuste: Long,
        idUsuario: Long
    ): Ajuste {
        TODO("Not yet implemented")
    }

}