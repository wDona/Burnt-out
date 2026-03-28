package dev.wdona.burntout.data.dao.impl

import dev.wdona.burntout.data.dao.AjusteDao
import dev.wdona.burntout.data.datasource.mapper.AjusteMapper
import dev.wdona.burntout.domain.model.Ajuste
import dev.wdona.burntout.shared.db.AppDatabase

class AjusteDaoImpl(appDatabase: AppDatabase) : AjusteDao {
    private val queries = appDatabase.appDatabaseQueries

    override fun anadirAjuste(ajuste: Ajuste) {
        TODO()
    }

    override fun modificarAjuste(idUsuario: Long, ajuste: Ajuste) {
        TODO("Not yet implemented")
    }

    override fun eliminarAjuste(idAjuste: Long) {
        TODO("Not yet implemented")
    }

    override fun getAjustesByUsuario(
        idUsuario: Long
    ): List<Ajuste> {
        return AjusteMapper.toDomainFromGetAjustesByUsuario(
            queries.getAjustesByUsuario(
                idUsuario
            ).executeAsList())
    }

    override fun getAjusteByIdYUsuario(
        idAjuste: Long,
        idUsuario: Long
    ): Ajuste {
        return AjusteMapper.toDomain(
            queries.getAjusteByIdYUsuario(
                idAjuste,
                idUsuario
            ).executeAsOne())
    }
}