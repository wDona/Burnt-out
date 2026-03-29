package dev.wdona.burntout.data.datasource.remote.impl

import dev.wdona.burntout.data.api.UsuarioApi
import dev.wdona.burntout.data.datasource.remote.UsuarioRemoteDataSource
import dev.wdona.burntout.shared.domain.Usuario

class UsuarioRemoteDataSourceImpl(private val usuarioApi: UsuarioApi) : UsuarioRemoteDataSource {
    override suspend fun getUserById(idUsuario: Long): Usuario {
        return usuarioApi.getUserById(idUsuario)
    }

    override suspend fun getUsuariosByOrg(idOrg: Long): List<Usuario> {
        return usuarioApi.getUsuariosByOrg(idOrg)
    }

    override suspend fun crearUsuario(usuario: Usuario): Long {
        return usuarioApi.crearUsuario(usuario)
    }

    override suspend fun actualizarUsuario(usuario: Usuario): Boolean {
        return usuarioApi.actualizarUsuario(usuario)
    }

    override suspend fun eliminarUsuario(idUsuario: Long): Boolean {
        return usuarioApi.eliminarUsuario(idUsuario)
    }

    override suspend fun existeUsuario(username: String): Boolean {
        return usuarioApi.existeUsuario(username)
    }

    override suspend fun login(username: String, contrasena: String): Usuario {
        return usuarioApi.login(username, contrasena)
    }

    override suspend fun getMiembrosEquipo(idEquipo: Long): List<Usuario> {
        return usuarioApi.getMiembrosEquipo(idEquipo)
    }

    override suspend fun getUsuarioByUsername(username: String): Usuario? {
        return usuarioApi.getUsuarioByUsername(username)
    }
}
