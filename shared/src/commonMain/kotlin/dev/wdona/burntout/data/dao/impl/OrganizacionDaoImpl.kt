package dev.wdona.burntout.data.dao.impl

import dev.wdona.burntout.data.dao.OrganizacionDao
import dev.wdona.burntout.data.datasource.mapper.OrganizacionMapper
import dev.wdona.burntout.shared.db.AppDatabase
import dev.wdona.burntout.shared.domain.Organizacion
import dev.wdona.burntout.shared.utils.Logger
import dev.wdona.burntout.shared.utils.getCurrentTimestampSeconds

class OrganizacionDaoImpl(appDatabase: AppDatabase) : OrganizacionDao {
    private val queries = appDatabase.appDatabaseQueries
    private val TAG = "OrganizacionDaoImpl"

    override suspend fun getOrganizacionById(idOrg: Long): Organizacion? {
        Logger.d(TAG, "getOrganizacionById: $idOrg")
        return queries.getOrganizacionById(idOrg).executeAsOneOrNull()?.let {
            OrganizacionMapper.toDomain(it)
        }
    }

    override suspend fun getAllOrganizaciones(): List<Organizacion> {
        Logger.d(TAG, "getAllOrganizaciones")
        return queries.getAllOrganizaciones().executeAsList().map {
            OrganizacionMapper.toDomain(it)
        }
    }

    override suspend fun insertOrUpdateOrganizacion(organizacion: Organizacion): Boolean {
        Logger.d(TAG, "insertOrUpdateOrganizacion: ${organizacion.idOrganizacion}")
        return try {
            queries.upsertOrganizacion(
                ID_Org = organizacion.idOrganizacion,
                Org_Name = organizacion.nombre,
                Is_Deleted = if (organizacion.isDeleted) 1L else 0L,
                Updated_At = getCurrentTimestampSeconds()
            )
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error insertOrUpdateOrganizacion: ${e.message}")
            false
        }
    }

    override suspend fun actualizarOrganizacion(organizacion: Organizacion): Boolean {
        Logger.d(TAG, "actualizarOrganizacion: ${organizacion.idOrganizacion}")
        return try {
            queries.updateOrganizacion(
                Org_Name = organizacion.nombre,
                ID_Org = organizacion.idOrganizacion,
                Updated_At = getCurrentTimestampSeconds()
            )
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error actualizarOrganizacion: ${e.message}")
            false
        }
    }

    override suspend fun eliminarOrganizacion(idOrg: Long): Boolean {
        Logger.d(TAG, "eliminarOrganizacion: $idOrg")
        return try {
            queries.deleteOrganizacion(getCurrentTimestampSeconds(), idOrg)
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error eliminarOrganizacion: ${e.message}")
            false
        }
    }

    override suspend fun insertPreguntasMBI(idOrg: Long) {
        Logger.d(TAG, "insertPreguntasMBI: $idOrg")
        try {
            queries.insertPreguntasMBI(idOrg)
        } catch (e: Exception) {
            Logger.d(TAG, "Error insertPreguntasMBI: ${e.message}")
        }
    }
}