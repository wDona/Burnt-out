package dev.wdona.burntout.data.api

import dev.wdona.burntout.shared.domain.Organizacion
import io.ktor.client.statement.HttpResponse

interface OrganizacionApi {
    suspend fun getOrganizacionById(idOrg: Long): Organizacion?
    suspend fun getAllOrganizaciones(): List<Organizacion>
    suspend fun crearOrganizacion(organizacion: Organizacion): HttpResponse
    suspend fun actualizarOrganizacion(organizacion: Organizacion): HttpResponse
    suspend fun eliminarOrganizacion(idOrg: Long): HttpResponse
}