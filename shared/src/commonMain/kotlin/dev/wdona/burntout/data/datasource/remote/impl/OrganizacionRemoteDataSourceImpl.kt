package dev.wdona.burntout.data.datasource.remote.impl

import dev.wdona.burntout.data.api.OrganizacionApi
import dev.wdona.burntout.data.datasource.remote.OrganizacionRemoteDataSource
import dev.wdona.burntout.shared.domain.Organizacion
import io.ktor.http.isSuccess

class OrganizacionRemoteDataSourceImpl(private val api: OrganizacionApi) : OrganizacionRemoteDataSource {

    override suspend fun getOrganizacionById(idOrg: Long): Organizacion? =
        api.getOrganizacionById(idOrg)

    override suspend fun getAllOrganizaciones(): List<Organizacion> =
        api.getAllOrganizaciones()

    override suspend fun crearOrganizacion(organizacion: Organizacion): Long {
        val response = api.crearOrganizacion(organizacion)
        return if (response.status.isSuccess()) organizacion.idOrganizacion else -1L
    }

    override suspend fun actualizarOrganizacion(organizacion: Organizacion): Boolean =
        api.actualizarOrganizacion(organizacion).status.isSuccess()

    override suspend fun eliminarOrganizacion(idOrg: Long): Boolean =
        api.eliminarOrganizacion(idOrg).status.isSuccess()
}