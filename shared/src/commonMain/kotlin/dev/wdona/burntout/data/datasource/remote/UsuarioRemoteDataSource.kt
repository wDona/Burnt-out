package dev.wdona.burntout.data.datasource.remote

import dev.wdona.burntout.shared.domain.Usuario

interface UsuarioRemoteDataSource {
    suspend fun getUserById(idUsuario: Long): Usuario
    suspend fun getUsuariosByOrg(idOrg: Long): List<Usuario>
    suspend fun crearUsuario(usuario: Usuario): Boolean
    suspend fun actualizarUsuario(usuario: Usuario): Boolean
    suspend fun eliminarUsuario(idUsuario: Long): Boolean
    suspend fun login(username: String, contrasena: String): Usuario
}
