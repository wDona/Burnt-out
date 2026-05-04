package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.EquipoApi
import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.domain.Usuario
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
import io.ktor.http.isSuccess

class EquipoApiImpl(private val client: HttpClient = ApiClient.client) : EquipoApi {
    private val TAG = "EquipoApiImpl"

    override suspend fun getEquipoById(idEquipo: Long): Equipo {
        Logger.d(TAG, "getEquipoById: $idEquipo")
        return client.get("equipos/$idEquipo").body()
    }

    override suspend fun getEquiposByOrg(idOrg: Long): List<Equipo> {
        Logger.d(TAG, "getEquiposByOrg: $idOrg")
        return client.get("equipos?idOrg=$idOrg").body()
    }

    override suspend fun crearEquipo(equipo: Equipo): Equipo {
        Logger.d(TAG, "crearEquipo: $equipo")
        return client.post("equipos") {
            contentType(ContentType.Application.Json)
            setBody(equipo)
        }.body()
    }

    override suspend fun actualizarEquipo(equipo: Equipo): Boolean {
        Logger.d(TAG, "actualizarEquipo: $equipo")
        return client.put("equipos/${equipo.idEquipo}") {
            contentType(ContentType.Application.Json)
            setBody(equipo)
        }.status.isSuccess()
    }

    override suspend fun eliminarEquipo(idEquipo: Long): Boolean {
        Logger.d(TAG, "eliminarEquipo: $idEquipo")
        return client.delete("equipos/$idEquipo").status.isSuccess()
    }

    override suspend fun getMiembrosEquipo(idEquipo: Long): List<Usuario> {
        Logger.d(TAG, "getMiembrosEquipo: $idEquipo")
        return client.get("equipos/$idEquipo/miembros").body()
    }

    override suspend fun addUsuarioAlEquipo(idEquipo: Long, idUsuario: Long): Boolean {
        Logger.d(TAG, "addUsuarioAlEquipo: equipo=$idEquipo, user=$idUsuario")
        return client.post("equipos/$idEquipo/miembros/$idUsuario").status.isSuccess()
    }

    override suspend fun removeUsuarioDelEquipo(idEquipo: Long, idUsuario: Long): Boolean {
        Logger.d(TAG, "removeUsuarioDelEquipo: equipo=$idEquipo, user=$idUsuario")
        return client.delete("equipos/$idEquipo/miembros/$idUsuario").status.isSuccess()
    }

    override suspend fun updatePuntuacion(idEquipo: Long, puntos: Long): Boolean {
        Logger.d(TAG, "updatePuntuacion: equipo=$idEquipo, puntos=$puntos")
        return client.put("equipos/$idEquipo/puntuacion/$puntos").status.isSuccess()
    }
}
