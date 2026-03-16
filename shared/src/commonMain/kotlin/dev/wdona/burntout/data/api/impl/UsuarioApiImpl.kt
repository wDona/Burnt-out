package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.UsuarioApi
import dev.wdona.burntout.shared.domain.Usuario

class UsuarioApiImpl : UsuarioApi {
    override suspend fun getUserById(idUsuario: Long): Usuario {
//        return Usuario(idUsuario, "nombre api", "pass", "Nombre api", 0.1, "Descripcion aoehudtanoeusahoenuts aotnehuano  tuahoteus  utahs", 1, 1)
        throw Exception("No hay conexion todavia")
    }

    override suspend fun getUsuariosByOrg(idOrg: Long): List<Usuario> {
        throw Exception("No hay conexion todavia")
    }

    override suspend fun crearUsuario(usuario: Usuario): Boolean {
        throw Exception("No hay conexion todavia")
    }

    override suspend fun actualizarUsuario(usuario: Usuario): Boolean {
        throw Exception("No hay conexion todavia")
    }

    override suspend fun eliminarUsuario(idUsuario: Long): Boolean {
        throw Exception("No hay conexion todavia")
    }

    override suspend fun login(username: String, contrasena: String): Usuario {
        throw Exception("No hay conexion todavia")
    }
}
