package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.OrganizacionApi
import dev.wdona.burntout.shared.domain.Organizacion
import dev.wdona.burntout.shared.network.ApiClient
import dev.wdona.burntout.shared.utils.Logger
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
    private val TAG = "OrganizacionApiImpl"

    override suspend fun getOrganizacionById(idOrg: Long): Organizacion? {
        Logger.d(TAG, "getOrganizacionById: $idOrg")
        return try { client.get("organizaciones/$idOrg").body() } catch (e: Exception) { null }
    }

    override suspend fun getAllOrganizaciones(): List<Organizacion> {
        Logger.d(TAG, "getAllOrganizaciones")
        return client.get("organizaciones").body()
    }

    override suspend fun crearOrganizacion(organizacion: Organizacion): HttpResponse {
        Logger.d(TAG, "crearOrganizacion: $organizacion")
        return client.post("organizaciones") {
            contentType(ContentType.Application.Json)
            setBody(organizacion)
        }
    }

    override suspend fun actualizarOrganizacion(organizacion: Organizacion): HttpResponse {
        Logger.d(TAG, "actualizarOrganizacion: $organizacion")
        return client.put("organizaciones/${organizacion.idOrganizacion}") {
            contentType(ContentType.Application.Json)
            setBody(organizacion)
        }
    }

    override suspend fun eliminarOrganizacion(idOrg: Long): HttpResponse {
        Logger.d(TAG, "eliminarOrganizacion: $idOrg")
        return client.delete("organizaciones/$idOrg")
    }
}