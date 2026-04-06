package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.EquipoApi
import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.domain.Usuario
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
import io.ktor.http.isSuccess

class EquipoApiImpl(private val client: HttpClient = ApiClient.client) : EquipoApi {
    override suspend fun getEquipoById(idEquipo: Long): Equipo =
        client.get("equipos/$idEquipo").body()

    override suspend fun getEquiposByOrg(idOrg: Long): List<Equipo> =
        client.get("equipos?idOrg=$idOrg").body()

    override suspend fun crearEquipo(equipo: Equipo): Equipo =
        client.post("equipos") {
            contentType(ContentType.Application.Json)
            setBody(equipo)
        }.body()

    override suspend fun actualizarEquipo(equipo: Equipo): Boolean =
        client.put("equipos/${equipo.idEquipo}") {
            contentType(ContentType.Application.Json)
            setBody(equipo)
        }.status.isSuccess()

    override suspend fun eliminarEquipo(idEquipo: Long): Boolean =
        client.delete("equipos/$idEquipo").status.isSuccess()

    override suspend fun getMiembrosEquipo(idEquipo: Long): List<Usuario> =
        client.get("equipos/$idEquipo/miembros").body()

    override suspend fun addUsuarioAlEquipo(idEquipo: Long, idUsuario: Long): Boolean =
        client.post("equipos/$idEquipo/miembros/$idUsuario").status.isSuccess()

    override suspend fun removeUsuarioDelEquipo(idEquipo: Long, idUsuario: Long): Boolean =
        client.delete("equipos/$idEquipo/miembros/$idUsuario").status.isSuccess()
}
