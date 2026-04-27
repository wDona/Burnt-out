package dev.wdona.burntout.data.datasource.local.impl

import dev.wdona.burntout.data.dao.OrganizacionDao
import dev.wdona.burntout.data.datasource.local.OrganizacionLocalDataSource
import dev.wdona.burntout.shared.domain.Organizacion

class OrganizacionLocalDataSourceImpl(private val dao: OrganizacionDao) : OrganizacionLocalDataSource {

    override suspend fun getOrganizacionById(idOrg: Long): Organizacion? =
        dao.getOrganizacionById(idOrg)

    override suspend fun getAllOrganizaciones(): List<Organizacion> =
        dao.getAllOrganizaciones()

    override suspend fun crearOrganizacion(organizacion: Organizacion): Long {
        dao.insertOrUpdateOrganizacion(organizacion)
        return organizacion.idOrganizacion
    }

    override suspend fun insertOrUpdateOrganizacion(organizacion: Organizacion): Boolean =
        dao.insertOrUpdateOrganizacion(organizacion)

    override suspend fun actualizarOrganizacion(organizacion: Organizacion): Boolean =
        dao.actualizarOrganizacion(organizacion)

    override suspend fun eliminarOrganizacion(idOrg: Long): Boolean =
        dao.eliminarOrganizacion(idOrg)

    override suspend fun insertPreguntasMBI(idOrg: Long) {
        dao.insertPreguntasMBI(idOrg)
    }
}