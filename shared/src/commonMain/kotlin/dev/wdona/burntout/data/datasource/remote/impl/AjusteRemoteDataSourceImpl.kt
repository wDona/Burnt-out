package dev.wdona.burntout.data.datasource.remote.impl

import dev.wdona.burntout.data.api.AjusteApi
import dev.wdona.burntout.data.datasource.remote.AjusteRemoteDataSource
import dev.wdona.burntout.domain.model.Ajuste

class AjusteRemoteDataSourceImpl(private val api: AjusteApi) : AjusteRemoteDataSource {
    override suspend fun anadirAjuste(idUsuario: Long, ajuste: Ajuste): Ajuste {
        return api.anadirAjuste(idUsuario, ajuste)
    }

    override suspend fun modificarAjuste(idUsuario: Long, ajuste: Ajuste) {
        api.modificarAjuste(idUsuario, ajuste)
    }

    override suspend fun getAjustesByUsuario(idUsuario: Long): List<Ajuste> {
        return api.getAjustes(idUsuario)
    }

    override suspend fun getAjusteByIdYUsuario(
        idAjuste: Long,
        idUsuario: Long
    ): Ajuste {
        val ajustes = api.getAjustes(idUsuario)
        return ajustes.first { it.idAjuste == idAjuste }
    }

}