package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.OrganizacionApi
import dev.wdona.burntout.shared.domain.Organizacion
import dev.wdona.burntout.shared.network.ApiClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.statement.HttpResponse

class OrganizacionApiImpl(private val client: HttpClient = ApiClient.client) : OrganizacionApi {

    override suspend fun getOrganizacionById(idOrg: Long): Organizacion? =
        try { client.get("organizaciones/$idOrg").body() } catch (e: Exception) { null }

    override suspend fun getAllOrganizaciones(): List<Organizacion> =
        client.get("organizaciones").body()

    override suspend fun crearOrganizacion(organizacion: Organizacion): HttpResponse =
        client.post("organizaciones") {
            contentType(ContentType.Application.Json)
            setBody(organizacion)
        }

    override suspend fun actualizarOrganizacion(organizacion: Organizacion): HttpResponse =
        client.put("organizaciones/${organizacion.idOrganizacion}") {
            contentType(ContentType.Application.Json)
            setBody(organizacion)
        }

    override suspend fun eliminarOrganizacion(idOrg: Long): HttpResponse =
        client.delete("organizaciones/$idOrg")
}