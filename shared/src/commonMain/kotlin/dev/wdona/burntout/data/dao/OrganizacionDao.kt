package dev.wdona.burntout.data.dao

import dev.wdona.burntout.shared.domain.Organizacion

interface OrganizacionDao {
    suspend fun getOrganizacionById(idOrg: Long): Organizacion?
    suspend fun getAllOrganizaciones(): List<Organizacion>
    suspend fun insertOrUpdateOrganizacion(organizacion: Organizacion): Boolean
    suspend fun actualizarOrganizacion(organizacion: Organizacion): Boolean
    suspend fun eliminarOrganizacion(idOrg: Long): Boolean
}