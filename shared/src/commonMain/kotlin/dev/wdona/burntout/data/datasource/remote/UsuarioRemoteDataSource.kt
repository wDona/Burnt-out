package dev.wdona.burntout.data.datasource.remote

import dev.wdona.burntout.shared.domain.RegistroRequest
import dev.wdona.burntout.shared.domain.Usuario

interface UsuarioRemoteDataSource {
    suspend fun registrar(request: RegistroRequest): Usuario
    suspend fun getUserById(idUsuario: Long): Usuario
    suspend fun getUsuariosByOrg(idOrg: Long): List<Usuario>
    suspend fun getUsuarioByUsername(username: String): Usuario?
    suspend fun crearUsuario(usuario: Usuario): Long
    suspend fun actualizarUsuario(usuario: Usuario): Boolean
    suspend fun eliminarUsuario(idUsuario: Long): Boolean
    suspend fun existeUsuario(username: String): Boolean
    suspend fun login(username: String, contrasena: String): Usuario
    suspend fun getMiembrosEquipo(idEquipo: Long): List<Usuario>
}
