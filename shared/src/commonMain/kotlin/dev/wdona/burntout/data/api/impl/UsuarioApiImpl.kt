package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.UsuarioApi
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
import io.ktor.http.HttpStatusCode
import dev.wdona.burntout.shared.domain.RegistroRequest
import kotlinx.serialization.Serializable

@Serializable
private data class LoginRequest(val username: String, val contrasena: String)

class UsuarioApiImpl(private val client: HttpClient = ApiClient.client) : UsuarioApi {
    private val TAG = "UsuarioApiImpl"

    override suspend fun getUserById(idUsuario: Long): Usuario =
        client.get("usuarios/$idUsuario").body<Usuario>().also { Logger.d(TAG, "getUserById: $it") }

    override suspend fun getUsuariosByOrg(idOrg: Long): List<Usuario> =
        client.get("usuarios?idOrg=$idOrg").body<List<Usuario>>().also { Logger.d(TAG, "getUsuariosByOrg: $it") }

    override suspend fun registrar(request: RegistroRequest): Usuario =
        client.post("usuarios") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<Usuario>().also { Logger.d(TAG, "registrar: $it") }

    override suspend fun crearUsuario(usuario: Usuario): Long =
        client.post("usuarios") {
            contentType(ContentType.Application.Json)
            setBody(RegistroRequest(
                username = usuario.username,
                password = usuario.password,
                nombre = usuario.nombre,
                modo = "CREAR_ORG",
                nombreOrg = "Org de ${usuario.nombre}"
            ))
        }.body<Usuario>().also { Logger.d(TAG, "crearUsuario: $it") }.idUsuario

    override suspend fun actualizarUsuario(usuario: Usuario): Boolean =
        client.put("usuarios/${usuario.idUsuario}") {
            contentType(ContentType.Application.Json)
            setBody(usuario)
        }.status.isSuccess().also { Logger.d(TAG, "actualizarUsuario: $usuario, success=$it") }

    override suspend fun eliminarUsuario(idUsuario: Long): Boolean =
        client.delete("usuarios/$idUsuario").status.isSuccess().also { Logger.d(TAG, "eliminarUsuario: $idUsuario, success=$it") }

    override suspend fun existeUsuario(username: String): Boolean =
        client.get("usuarios/existe/$username") {
            contentType(ContentType.Application.Json)
        }.body<Boolean>().also { Logger.d(TAG, "existeUsuario: $username, result=$it") }

    override suspend fun getUsuarioByUsername(username: String): Usuario? =
        client.get("usuarios/username/$username") {
            contentType(ContentType.Application.Json)
        }.let { response ->
            if (response.status == HttpStatusCode.NotFound) null
            else response.body<Usuario>()
        }.also { Logger.d(TAG, "getUsuarioByUsername: $username, result=$it") }

    override suspend fun login(username: String, contrasena: String): Usuario =
        client.post("usuarios/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(username, contrasena))
        }.body<Usuario>().also { Logger.d(TAG, "login: $it") }

    override suspend fun getMiembrosEquipo(idEquipo: Long): List<Usuario> =
        client.get("equipos/$idEquipo/miembros").body<List<Usuario>>().also { Logger.d(TAG, "getMiembrosEquipo: $it") }
}
