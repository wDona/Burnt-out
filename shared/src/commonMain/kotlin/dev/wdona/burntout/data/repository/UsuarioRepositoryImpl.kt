package dev.wdona.burntout.data.repository

import dev.wdona.burntout.data.datasource.local.OperacionPendienteLocalDataSource
import dev.wdona.burntout.data.datasource.local.UsuarioLocalDataSource
import dev.wdona.burntout.data.datasource.mapper.UsuarioMapper
import dev.wdona.burntout.data.datasource.remote.UsuarioRemoteDataSource
import dev.wdona.burntout.domain.entity.Entity
import dev.wdona.burntout.domain.model.TipoAccion
import dev.wdona.burntout.domain.repository.UsuarioRepository
import dev.wdona.burntout.shared.domain.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsuarioRepositoryImpl(
    private val local: UsuarioLocalDataSource,
    private val remote: UsuarioRemoteDataSource,
    private val pendiente: OperacionPendienteLocalDataSource
) : UsuarioRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Default)

    override suspend fun getUserById(idUsuario: Long): Usuario = withContext(Dispatchers.IO) {
        if (idUsuario == Long.MIN_VALUE) {
            return@withContext local.getUserById(idUsuario)
        }
        try {
            val usuario = remote.getUserById(idUsuario)
            if (usuario != null) {
                local.eliminarUsuario(usuario.idUsuario)
                local.crearUsuario(usuario)
            }
            usuario
        } catch (e: Exception) {
            println("Servidor offline (getUserById): ${e.message}")
            local.getUserById(idUsuario)
        }

    }

    override suspend fun getUsuariosByOrg(idOrg: Long): List<Usuario> = withContext(Dispatchers.IO) {
        repositoryScope.launch {
            try {
                val usuarios = remote.getUsuariosByOrg(idOrg)
                local.eliminarUsuariosPorOrg(idOrg)
                usuarios.forEach { local.crearUsuario(it) }
            } catch (e: Exception) {
                println("Servidor offline (getUsuariosByOrg): ${e.message}")
            }
        }
        local.getUsuariosByOrg(idOrg)
    }

    override suspend fun getUsuariosByEquipo(idEquipo: Long): List<Usuario> = withContext(Dispatchers.IO) {
        // FIXME: Sincronizar remote si existe endpoint
        local.getUsuariosByEquipo(idEquipo)
    }

    override suspend fun crearUsuario(usuario: Usuario) {
        withContext(Dispatchers.IO) {
            try {
                local.crearUsuario(usuario)
            } catch (e: Exception) {
                println("Error local al crear usuario: ${e.message}")
            }
        }

        if (usuario.idUsuario == Long.MIN_VALUE) return

        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            try {
                exito = remote.crearUsuario(usuario)
            } catch (e: Exception) {
                println("Servidor offline al crear usuario: ${e.message}")
            }
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.CREACION.getNombreAccion(),
                    Entity.USUARIO.getNombreEntity(),
                    usuario.idUsuario,
                    UsuarioMapper.toJson(usuario),
                    System.currentTimeMillis(),
                    if (exito) 1L else 0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }

    override suspend fun actualizarUsuario(usuario: Usuario) {
        withContext(Dispatchers.IO) {
            try {
                local.actualizarUsuario(usuario)
            } catch (e: Exception) {
                println("Error local al actualizar usuario: ${e.message}")
            }
        }

        if (usuario.idUsuario == Long.MIN_VALUE) return

        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            try {
                exito = remote.actualizarUsuario(usuario)
            } catch (e: Exception) {
                println("Servidor offline al actualizar usuario: ${e.message}")
            }
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.ACTUALIZACION.getNombreAccion(),
                    Entity.USUARIO.getNombreEntity(),
                    usuario.idUsuario,
                    UsuarioMapper.toJson(usuario),
                    System.currentTimeMillis(),
                    if (exito) 1L else 0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }

    override suspend fun eliminarUsuario(idUsuario: Long) {
        withContext(Dispatchers.IO) {
            try {
                local.eliminarUsuario(idUsuario)
            } catch (e: Exception) {
                println("Error local al eliminar usuario: ${e.message}")
            }
        }

        if (idUsuario == Long.MIN_VALUE) return

        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            try {
                exito = remote.eliminarUsuario(idUsuario)
            } catch (e: Exception) {
                println("Servidor offline al eliminar usuario: ${e.message}")
            }
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.ELIMINACION.getNombreAccion(),
                    Entity.USUARIO.getNombreEntity(),
                    idUsuario,
                    "",
                    System.currentTimeMillis(),
                    if (exito) 1L else 0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }

    override suspend fun updateRiesgoBurnout(idUsuario: Long, riesgo: Double) {
        withContext(Dispatchers.IO) {
            try {
                local.updateRiesgoBurnout(idUsuario, riesgo)
            } catch (e: Exception) {
                println("Error al actualizar riesgo: ${e.message}")
            }
        }
    }

    override suspend fun login(username: String, contrasena: String): Usuario = withContext(Dispatchers.IO) {
        val usuario = remote.login(username, contrasena)
        local.crearUsuario(usuario) // FIXME?
        usuario
    }
}