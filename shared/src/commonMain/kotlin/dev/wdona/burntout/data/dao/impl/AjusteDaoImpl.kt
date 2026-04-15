package dev.wdona.burntout.data.dao.impl

import dev.wdona.burntout.data.dao.AjusteDao
import dev.wdona.burntout.data.datasource.mapper.AjusteMapper
import dev.wdona.burntout.domain.model.Ajuste
import dev.wdona.burntout.shared.db.AppDatabase
import dev.wdona.burntout.shared.utils.Logger

class AjusteDaoImpl(appDatabase: AppDatabase) : AjusteDao {
    private val queries = appDatabase.appDatabaseQueries
    private val TAG = "AjusteDaoImpl"

    override fun anadirAjuste(ajuste: Ajuste) {
        Logger.d(TAG, "anadirAjuste: ${ajuste.idAjuste}")
        TODO()
    }

    override fun modificarAjuste(idUsuario: Long, ajuste: Ajuste) {
        Logger.d(TAG, "modificarAjuste: user=$idUsuario, ajuste=${ajuste.idAjuste}")
        TODO("Not yet implemented")
    }

    override fun eliminarAjuste(idAjuste: Long) {
        Logger.d(TAG, "eliminarAjuste: $idAjuste")
        TODO("Not yet implemented")
    }

    override fun getAjustesByUsuario(
        idUsuario: Long
    ): List<Ajuste> {
        Logger.d(TAG, "getAjustesByUsuario: $idUsuario")
        return AjusteMapper.toDomainFromGetAjustesByUsuario(
            queries.getAjustesByUsuario(
                idUsuario
            ).executeAsList())
    }

    override fun getAjusteByIdYUsuario(
        idAjuste: Long,
        idUsuario: Long
    ): Ajuste {
        Logger.d(TAG, "getAjusteByIdYUsuario: ajuste=$idAjuste, user=$idUsuario")
        return AjusteMapper.toDomain(
            queries.getAjusteByIdYUsuario(
                idAjuste,
                idUsuario
            ).executeAsOne())
    }
}