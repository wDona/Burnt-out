package dev.wdona.burntout.data.api

import dev.wdona.burntout.shared.domain.LoginResponse
import dev.wdona.burntout.shared.domain.RegistroRequest
import dev.wdona.burntout.shared.domain.Usuario

interface UsuarioApi {
    suspend fun getUserById(idUsuario: Long): Usuario
    suspend fun getUsuariosByOrg(idOrg: Long): List<Usuario>
    suspend fun registrar(request: RegistroRequest): LoginResponse
    suspend fun crearUsuario(usuario: Usuario): Long
    suspend fun actualizarUsuario(usuario: Usuario): Boolean
    suspend fun eliminarUsuario(idUsuario: Long): Boolean
    suspend fun eliminarUsuarioComoAdmin(idAdmin: Long, idUsuario: Long): Boolean
    suspend fun existeUsuario(username: String): Boolean
    suspend fun getUsuarioByUsername(username: String): Usuario?
    suspend fun login(username: String, contrasena: String): LoginResponse
    suspend fun getMiembrosEquipo(idEquipo: Long): List<Usuario>
    suspend fun updateRol(idAdmin: Long, idUsuario: Long, nuevoRol: String): Boolean
    suspend fun cerrarSesion(token: String): Boolean
}
