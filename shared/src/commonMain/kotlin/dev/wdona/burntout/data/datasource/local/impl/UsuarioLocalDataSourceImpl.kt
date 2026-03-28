package dev.wdona.burntout.data.datasource.local.impl

import dev.wdona.burntout.data.dao.UsuarioDao
import dev.wdona.burntout.data.datasource.local.UsuarioLocalDataSource
import dev.wdona.burntout.shared.domain.Usuario

class UsuarioLocalDataSourceImpl(private val usuarioDao: UsuarioDao) : UsuarioLocalDataSource {
    override suspend fun getUserById(idUsuario: Long): Usuario {
        return usuarioDao.getUserById(idUsuario)
    }

    override suspend fun getUsuariosByOrg(idOrg: Long): List<Usuario> {
        return usuarioDao.getUsuariosByOrg(idOrg)
    }

    override suspend fun getUsuariosByEquipo(idEquipo: Long): List<Usuario> {
        return usuarioDao.getUsuariosByEquipo(idEquipo)
    }

    override suspend fun getUsuarioByUsername(username: String): Usuario {
        return usuarioDao.getUsuarioByUsername(username)
    }

    override suspend fun crearUsuario(usuario: Usuario): Long {
        return usuarioDao.crearUsuario(usuario)
    }

    override suspend fun actualizarUsuario(usuario: Usuario): Boolean {
        return usuarioDao.actualizarUsuario(usuario)
    }

    override suspend fun eliminarUsuario(idUsuario: Long): Boolean {
        return usuarioDao.eliminarUsuario(idUsuario)
    }

    override suspend fun insertOrUpdateUsuario(usuario: Usuario): Boolean {
        return usuarioDao.insertOrUpdateUsuario(usuario)
    }

    override suspend fun updateRiesgoBurnout(idUsuario: Long, riesgo: Double): Boolean {
        return usuarioDao.updateRiesgoBurnout(idUsuario, riesgo)
    }

    override suspend fun eliminarUsuariosPorOrg(idOrg: Long) {
        val usuarios = usuarioDao.getUsuariosByOrg(idOrg)
        usuarios.forEach { usuarioDao.eliminarUsuario(it.idUsuario) }
    }
}
