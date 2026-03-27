package dev.wdona.burntout.domain.repository

import dev.wdona.burntout.shared.domain.Organizacion

interface OrganizacionRepository {
    suspend fun getOrganizacionById(idOrg: Long): Organizacion?
    suspend fun getAllOrganizaciones(): List<Organizacion>
    suspend fun crearOrganizacion(organizacion: Organizacion)
    suspend fun actualizarOrganizacion(organizacion: Organizacion)
    suspend fun eliminarOrganizacion(idOrg: Long)
}