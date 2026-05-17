package dev.wdona.burntout.data.dao.impl

import dev.wdona.burntout.data.dao.AjusteDao
import dev.wdona.burntout.data.datasource.mapper.AjusteMapper
import dev.wdona.burntout.domain.model.Ajuste
import dev.wdona.burntout.shared.db.AppDatabase
import dev.wdona.burntout.shared.utils.Logger
import dev.wdona.burntout.shared.utils.SettingsManager
import dev.wdona.burntout.shared.utils.getCurrentTimestampSeconds

class AjusteDaoImpl(appDatabase: AppDatabase) : AjusteDao {
    private val queries = appDatabase.appDatabaseQueries
    private val TAG = "AjusteDaoImpl"

    override fun anadirAjuste(ajuste: Ajuste) {
        Logger.d(TAG, "anadirAjuste: ${ajuste.idAjuste}")
        queries.insertOrUpdateAjusteUser(
            ID_Ajuste = ajuste.idAjuste,
            FK_ID_Usuario = ajuste.idUsuario,
            Nombre_Ajuste = ajuste.nombre,
            Valor_Ajuste = ajuste.valorAjuste,
            Is_Deleted = if (ajuste.isDeleted) 1L else 0L,
            Updated_At = ajuste.updatedAt
        )
    }

    override fun modificarAjuste(idUsuario: Long, ajuste: Ajuste) {
        Logger.d(TAG, "modificarAjuste: user=$idUsuario, ajuste=${ajuste.idAjuste}")
        queries.insertOrUpdateAjusteUser(
            ID_Ajuste = ajuste.idAjuste,
            FK_ID_Usuario = idUsuario,
            Nombre_Ajuste = ajuste.nombre,
            Valor_Ajuste = ajuste.valorAjuste,
            Is_Deleted = if (ajuste.isDeleted) 1L else 0L,
            Updated_At = ajuste.updatedAt
        )
    }

    override fun eliminarAjuste(idAjuste: Long) {
        Logger.d(TAG, "eliminarAjuste: $idAjuste")
        val currentUserId = SettingsManager.idUsuarioActualFlow.value
        val existing = queries.getAjusteByIdYUsuario(currentUserId, idAjuste).executeAsOneOrNull()
        if (existing != null) {
            queries.insertOrUpdateAjusteUser(
                ID_Ajuste = idAjuste,
                FK_ID_Usuario = existing.FK_ID_Usuario,
                Nombre_Ajuste = existing.Nombre_Ajuste,
                Valor_Ajuste = existing.Valor_Ajuste,
                Is_Deleted = 1L,
                Updated_At = getCurrentTimestampSeconds()
            )
        }
    }

    override fun getAjustesByUsuario(
        idUsuario: Long
    ): List<Ajuste> {
        Logger.d(TAG, "getAjustesByUsuario: $idUsuario")
        return AjusteMapper.toDomainList(
            queries.getAjustesByUsuario(
                idUsuario
            ).executeAsList())
    }

    override fun getAjusteByIdYUsuario(
        idAjuste: Long,
        idUsuario: Long
    ): Ajuste? {
        Logger.d(TAG, "getAjusteByIdYUsuario: ajuste=$idAjuste, user=$idUsuario")
        return queries.getAjusteByIdYUsuario(
            idUsuario,
            idAjuste
        ).executeAsOneOrNull()?.let { AjusteMapper.toDomain(it) }
    }

    override fun insertOrUpdateAjuste(ajuste: Ajuste) {
        queries.insertOrUpdateAjusteUser(
            ID_Ajuste = ajuste.idAjuste,
            FK_ID_Usuario = ajuste.idUsuario,
            Nombre_Ajuste = ajuste.nombre,
            Valor_Ajuste = ajuste.valorAjuste,
            Is_Deleted = if (ajuste.isDeleted) 1L else 0L,
            Updated_At = ajuste.updatedAt
        )
    }

    override fun getAjusteByNombreYUsuario(nombre: String, idUsuario: Long): Ajuste? {
        return queries.getAjusteByNombreYUsuario(idUsuario, nombre).executeAsOneOrNull()
            ?.let { AjusteMapper.toDomain(it) }
    }
}