package dev.wdona.burntout.data.datasource.common

import dev.wdona.burntout.shared.domain.Organizacion

interface OrganizacionDataSource {
    suspend fun getOrganizacionById(idOrg: Long): Organizacion?
    suspend fun getAllOrganizaciones(): List<Organizacion>
    suspend fun crearOrganizacion(organizacion: Organizacion): Long
    suspend fun actualizarOrganizacion(organizacion: Organizacion): Boolean
    suspend fun eliminarOrganizacion(idOrg: Long): Boolean
}