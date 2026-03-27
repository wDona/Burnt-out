package dev.wdona.burntout.data.dao.impl

import dev.wdona.burntout.data.dao.OrganizacionDao
import dev.wdona.burntout.data.datasource.mapper.OrganizacionMapper
import dev.wdona.burntout.shared.db.AppDatabase
import dev.wdona.burntout.shared.domain.Organizacion

class OrganizacionDaoImpl(appDatabase: AppDatabase) : OrganizacionDao {
    private val queries = appDatabase.appDatabaseQueries

    override suspend fun getOrganizacionById(idOrg: Long): Organizacion? {
        return queries.getOrganizacionById(idOrg).executeAsOneOrNull()?.let {
            OrganizacionMapper.toDomain(it)
        }
    }

    override suspend fun getAllOrganizaciones(): List<Organizacion> {
        return queries.getAllOrganizaciones().executeAsList().map {
            OrganizacionMapper.toDomain(it)
        }
    }

    override suspend fun insertOrUpdateOrganizacion(organizacion: Organizacion): Boolean {
        return try {
            queries.upsertOrganizacion(
                ID_Org = organizacion.idOrganizacion,
                Org_Name = organizacion.nombre
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun actualizarOrganizacion(organizacion: Organizacion): Boolean {
        return try {
            queries.updateOrganizacion(
                Org_Name = organizacion.nombre,
                ID_Org = organizacion.idOrganizacion
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun eliminarOrganizacion(idOrg: Long): Boolean {
        return try {
            queries.deleteOrganizacion(idOrg)
            true
        } catch (e: Exception) {
            false
        }
    }
}