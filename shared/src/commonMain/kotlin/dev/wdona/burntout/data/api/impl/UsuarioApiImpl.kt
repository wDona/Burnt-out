package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.UsuarioApi
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
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

@Serializable
private data class LoginRequest(val username: String, val contrasena: String)

class UsuarioApiImpl(private val client: HttpClient = ApiClient.client) : UsuarioApi {
    override suspend fun getUserById(idUsuario: Long): Usuario =
        client.get("usuarios/$idUsuario").body()

    override suspend fun getUsuariosByOrg(idOrg: Long): List<Usuario> =
        client.get("usuarios?idOrg=$idOrg").body()

    override suspend fun crearUsuario(usuario: Usuario): Long {
        val response = client.post("usuarios") {
            contentType(ContentType.Application.Json)
            setBody(usuario)
        }
        return response.body<Usuario>().idUsuario
    }

    override suspend fun actualizarUsuario(usuario: Usuario): Boolean =
        client.put("usuarios/${usuario.idUsuario}") {
            contentType(ContentType.Application.Json)
            setBody(usuario)
        }.status.isSuccess()

    override suspend fun eliminarUsuario(idUsuario: Long): Boolean =
        client.delete("usuarios/$idUsuario").status.isSuccess()

    override suspend fun existeUsuario(username: String): Boolean {
        val response = client.get("usuarios/existe/$username") {
            contentType(ContentType.Application.Json)
        }
        return response.body()
    }

    override suspend fun getUsuarioByUsername(username: String): Usuario? {
        val response = client.get("usuarios/username/$username") {
            contentType(ContentType.Application.Json)
        }
        if (response.status == HttpStatusCode.NotFound) return null
        return response.body()
    }

    override suspend fun login(username: String, contrasena: String): Usuario =
        client.post("usuarios/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(username, contrasena))
        }.body()

    override suspend fun getMiembrosEquipo(idEquipo: Long): List<Usuario> {
        return client.get("equipos/$idEquipo/miembros").body()
    }
}
