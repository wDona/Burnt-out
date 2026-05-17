package dev.wdona.burntout.data.datasource.local.impl

import dev.wdona.burntout.data.dao.AjusteDao
import dev.wdona.burntout.data.datasource.local.AjusteLocalDataSource
import dev.wdona.burntout.domain.model.Ajuste

class AjusteLocalDataSourceImpl(private val dao: AjusteDao) : AjusteLocalDataSource {
    override suspend fun eliminarAjuste(idAjuste: Long) {
        dao.eliminarAjuste(idAjuste)
    }

    override suspend fun anadirAjuste(ajuste: Ajuste) {
        dao.anadirAjuste(ajuste)
    }

    override suspend fun modificarAjuste(idUsuario: Long, ajuste: Ajuste) {
        dao.modificarAjuste(idUsuario, ajuste)
    }

    override suspend fun getAjustesByUsuario(idUsuario: Long): List<Ajuste> {
        return dao.getAjustesByUsuario(idUsuario)
    }

    override suspend fun getAjusteByIdYUsuario(
        idAjuste: Long,
        idUsuario: Long
    ): Ajuste {
        return dao.getAjusteByIdYUsuario(idAjuste, idUsuario) ?: throw Exception("Ajuste no encontrado")
    }

    override suspend fun insertOrUpdateAjuste(ajuste: Ajuste) {
        dao.insertOrUpdateAjuste(ajuste)
    }

    override suspend fun getAjusteByNombreYUsuario(nombre: String, idUsuario: Long): Ajuste? {
        return dao.getAjusteByNombreYUsuario(nombre, idUsuario)
    }
}