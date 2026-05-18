package dev.wdona.burntout.data.repository

import dev.wdona.burntout.data.datasource.local.OperacionPendienteLocalDataSource
import dev.wdona.burntout.data.datasource.local.UsuarioLocalDataSource
import dev.wdona.burntout.data.datasource.mapper.UsuarioMapper
import dev.wdona.burntout.data.datasource.remote.UsuarioRemoteDataSource
import dev.wdona.burntout.domain.entity.Entity
import dev.wdona.burntout.domain.model.TipoAccion
import dev.wdona.burntout.domain.repository.UsuarioRepository
import dev.wdona.burntout.shared.domain.LoginResponse
import dev.wdona.burntout.shared.domain.RegistroRequest
import dev.wdona.burntout.shared.domain.Usuario
import dev.wdona.burntout.shared.utils.SettingsManager
import dev.wdona.burntout.shared.utils.getCurrentTimestampSeconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsuarioRepositoryImpl(
    private val local: UsuarioLocalDataSource,
    private val remote: UsuarioRemoteDataSource,
    private val pendiente: OperacionPendienteLocalDataSource
) : UsuarioRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Default)

    override suspend fun registrar(request: RegistroRequest): LoginResponse = withContext(NonCancellable + Dispatchers.IO) {
        val response = remote.registrar(request)
        local.insertOrUpdateUsuario(response.usuario)
        response
    }

    override suspend fun getUserById(idUsuario: Long): Usuario = withContext(NonCancellable + Dispatchers.IO) {
        if (idUsuario == Long.MIN_VALUE) {
            return@withContext local.getUserById(idUsuario)
        }
        try {
            val usuario = remote.getUserById(idUsuario)
            local.insertOrUpdateUsuario(usuario)
            usuario
        } catch (e: Exception) {
            println("Servidor offline (getUserById): ${e.message}")
            local.getUserById(idUsuario)
        }

    }

    override suspend fun getUsuariosByOrg(idOrg: Long): List<Usuario> = withContext(NonCancellable + Dispatchers.IO) {
        if (!SettingsManager.isUsuarioInvitado()) {
            repositoryScope.launch {
                try {
                    val usuarios = remote.getUsuariosByOrg(idOrg)
                    usuarios.forEach { local.insertOrUpdateUsuario(it) }
                } catch (e: Exception) {
                    println("Servidor offline (getUsuariosByOrg): ${e.message}")
                }
            }
        }
        local.getUsuariosByOrg(idOrg)
    }

    override suspend fun getUsuariosByEquipo(idEquipo: Long): List<Usuario> = withContext(NonCancellable + Dispatchers.IO) {
        if (!SettingsManager.isUsuarioInvitado()) {
            try {
                val usuarios = remote.getMiembrosEquipo(idEquipo)
                usuarios.forEach {
                    local.insertOrUpdateUsuario(it)
                    local.vincularUsuarioEquipo(it.idUsuario, idEquipo)
                }
                return@withContext usuarios
            } catch (e: Exception) {
                println("Error (getUsuariosByEquipo): ${e.message}")
            }
        }
        local.getUsuariosByEquipo(idEquipo)
    }

    override suspend fun crearUsuario(usuario: Usuario) {
        withContext(NonCancellable + Dispatchers.IO) {
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
                remote.crearUsuario(usuario)
                exito = true
            } catch (e: Exception) {
                println("Servidor offline al crear usuario: ${e.message}")
            }
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.CREACION.getNombreAccion(),
                    Entity.USUARIO.getNombreEntity(),
                    usuario.idUsuario.toString(),
                    UsuarioMapper.toJson(usuario),
                    getCurrentTimestampSeconds(),
                    if (exito) 1L else 0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }

    override suspend fun actualizarUsuario(usuario: Usuario) {
        withContext(NonCancellable + Dispatchers.IO) {
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
                    usuario.idUsuario.toString(),
                    UsuarioMapper.toJson(usuario),
                    getCurrentTimestampSeconds(),
                    if (exito) 1L else 0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }

    override suspend fun eliminarUsuario(idUsuario: Long) {
        withContext(NonCancellable + Dispatchers.IO) {
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
                    idUsuario.toString(),
                    "",
                    getCurrentTimestampSeconds(),
                    if (exito) 1L else 0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }

    override suspend fun eliminarUsuarioComoAdmin(idAdmin: Long, idUsuario: Long): Boolean =
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                val success = remote.eliminarUsuarioComoAdmin(idAdmin, idUsuario)
                if (success) {
                    try { local.eliminarUsuario(idUsuario) } catch (_: Exception) { }
                }
                success
            } catch (e: Exception) {
                println("Error al eliminar usuario como admin: ${e.message}")
                false
            }
        }

    override suspend fun updateRiesgoBurnout(idUsuario: Long, riesgo: Double) {
        withContext(NonCancellable + Dispatchers.IO) {
            val usuario = getUserById(idUsuario)
            actualizarUsuario(usuario.copy(riesgoBurnout = riesgo))
            local.updateRiesgoBurnout(idUsuario, riesgo)
        }
    }

    override suspend fun existeUsuario(username: String): Boolean = withContext(NonCancellable + Dispatchers.IO) {
        val existsRemote = try {
            remote.existeUsuario(username)
        } catch (e: Exception) {
            println("Error al comprobar existencia de usuario remota: ${e.message}")
            false
        }
        val existsLocal = try {
            local.getUsuarioByUsername(username) != null
        } catch (e: Exception) {
            false
        }
        existsRemote || existsLocal
    }

    override suspend fun getUsuarioByUsername(username: String): Usuario? = withContext(NonCancellable + Dispatchers.IO) {
        try {
            remote.getUsuarioByUsername(username)
        } catch (e: Exception) {
            try {
                local.getUsuarioByUsername(username)
            } catch (localE: Exception) {
                null
            }
        }
    }

    override suspend fun login(username: String, contrasena: String): LoginResponse = withContext(NonCancellable + Dispatchers.IO) {
        val response = remote.login(username, contrasena)
        local.insertOrUpdateUsuario(response.usuario)
        response
    }

    override suspend fun cerrarSesion(token: String) {
        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            try {
                exito = remote.cerrarSesion(token)
            } catch (e: Exception) {
                println("Servidor offline al cerrar sesión: ${e.message}")
            }
            if (!exito) {
                try {
                    pendiente.insertOperacionPendiente(
                        TipoAccion.ELIMINACION.getNombreAccion(),
                        Entity.SESION.getNombreEntity(),
                        token,
                        "",
                        getCurrentTimestampSeconds(),
                        0L
                    )
                } catch (e: Exception) {
                    println("Error al registrar cierre de sesión pendiente: ${e.message}")
                }
            }
        }
    }

    override suspend fun updateRol(idAdmin: Long, idUsuario: Long, nuevoRol: String): Boolean =
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                remote.updateRol(idAdmin, idUsuario, nuevoRol)
            } catch (e: Exception) {
                println("Error al actualizar rol: ${e.message}")
                false
            }
        }
}
