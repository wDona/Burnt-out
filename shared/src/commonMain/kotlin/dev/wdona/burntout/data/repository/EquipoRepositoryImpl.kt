package dev.wdona.burntout.data.repository

import dev.wdona.burntout.data.datasource.local.EquipoLocalDataSource
import dev.wdona.burntout.data.datasource.local.OperacionPendienteLocalDataSource
import dev.wdona.burntout.data.datasource.mapper.EquipoMapper
import dev.wdona.burntout.data.datasource.remote.EquipoRemoteDataSource
import dev.wdona.burntout.data.datasource.remote.UsuarioRemoteDataSource
import dev.wdona.burntout.domain.entity.Entity
import dev.wdona.burntout.domain.model.TipoAccion
import dev.wdona.burntout.domain.repository.EquipoRepository
import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.domain.Usuario
import dev.wdona.burntout.shared.utils.SettingsManager
import dev.wdona.burntout.shared.utils.getCurrentTimestampSeconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EquipoRepositoryImpl(
    private val local: EquipoLocalDataSource,
    private val remote: EquipoRemoteDataSource,
    private val remoteUsuario: UsuarioRemoteDataSource,
    private val pendiente: OperacionPendienteLocalDataSource,
) : EquipoRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Default)

    override suspend fun getEquiposByOrg(idOrg: Long): List<Equipo> = withContext(NonCancellable + Dispatchers.IO) {
        if (!SettingsManager.isUsuarioInvitado()) {
            repositoryScope.launch {
                try {
                    val equiposRemotos = remote.getEquiposByOrg(idOrg)
                    if (equiposRemotos.isNotEmpty()) {
                        equiposRemotos.forEach { local.insertOrUpdateEquipo(it) }
                    }
                } catch (e: Exception) {
                    println("Error al sincronizar equipos")
                }
            }
        }
        try {
            local.getEquiposByOrg(idOrg)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getEquipoById(idEquipo: Long): Equipo? = withContext(NonCancellable + Dispatchers.IO) {
        if (!SettingsManager.isUsuarioInvitado()) {
            try {
                val equipoRemoto = remote.getEquipoById(idEquipo)
                val miembros = remote.getMiembrosEquipo(idEquipo)
                val equipoConMiembros = equipoRemoto.copy(idMiembros = miembros.map { it.idUsuario })
                local.insertOrUpdateEquipo(equipoConMiembros)
                return@withContext equipoConMiembros
            } catch (e: Exception) {
                println("Error al sincronizar equipos")
            }
        }
        try {
            local.getEquipoById(idEquipo)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun crearEquipo(equipo: Equipo): Long = withContext(NonCancellable + Dispatchers.IO) {
        var localId = -1L
        try {
            localId = local.crearEquipo(equipo)
        } catch (e: Exception) {
            println("Error local al crear equipo: ${e.message}")
        }

        if (SettingsManager.isUsuarioInvitado()) return@withContext localId

        var idServidor = -1L
        try {
            val equipoRemoto = remote.crearEquipo(equipo)
            if (equipoRemoto != null) {
                idServidor = equipoRemoto.idEquipo
                // Actualizamos localmente con el ID real del servidor si es diferente
                local.insertOrUpdateEquipo(equipoRemoto)
            }
        } catch (e: Exception) {
            println("Servidor offline al crear equipo: ${e.message}")
        }

        val finalId = if (idServidor != -1L) idServidor else localId

        equipo.idMiembros.forEach { idMiembro ->
            try {
                local.addUsuarioAlEquipo(finalId, idMiembro)
            } catch (e: Exception) {
                println("Error al vincular miembro al equipo local: ${e.message}")
            }
        }

        try {
            pendiente.insertOperacionPendiente(
                TipoAccion.CREACION.getNombreAccion(),
                Entity.EQUIPO.getNombreEntity(),
                finalId.toString(),
                EquipoMapper.toJson(equipo.copy(idEquipo = finalId)),
                getCurrentTimestampSeconds(),
                if (idServidor != -1L) 1L else 0L
            )
        } catch (e: Exception) {
            println("Error al registrar operación pendiente: ${e.message}")
        }

        finalId
    }

    override suspend fun actualizarEquipo(equipo: Equipo) {
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                local.actualizarEquipo(equipo)
            } catch (e: Exception) {
                println("Error local al actualizar equipo: ${e.message}")
            }
        }

        if (SettingsManager.isUsuarioInvitado()) return

        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            try {
                exito = remote.actualizarEquipo(equipo)
            } catch (e: Exception) {
                println("Servidor invitado al actualizar equipo: ${e.message}")
            }
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.ACTUALIZACION.getNombreAccion(),
                    Entity.EQUIPO.getNombreEntity(),
                    equipo.idEquipo.toString(),
                    EquipoMapper.toJson(equipo),
                    getCurrentTimestampSeconds(),
                    if (exito) 1L else 0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }

    override suspend fun eliminarEquipo(idEquipo: Long) {
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                local.eliminarEquipo(idEquipo)
            } catch (e: Exception) {
                println("Error local al eliminar equipo: ${e.message}")
            }
        }

        if (SettingsManager.isUsuarioInvitado()) return

        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            try {
                exito = remote.eliminarEquipo(idEquipo)
            } catch (e: Exception) {
                println("Servidor invitado al eliminar equipo: ${e.message}")
            }
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.ELIMINACION.getNombreAccion(),
                    Entity.EQUIPO.getNombreEntity(),
                    idEquipo.toString(),
                    "",
                    getCurrentTimestampSeconds(),
                    if (exito) 1L else 0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }

    override suspend fun updatePuntuacion(idEquipo: Long, puntos: Long) {
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                local.updatePuntuacion(idEquipo, puntos)
            } catch (e: Exception) {
                println("Error local al actualizar puntuacion: ${e.message}")
            }
            if (SettingsManager.isUsuarioInvitado()) return@withContext
            var exito = false
            try {
                exito = remote.updatePuntuacion(idEquipo, puntos)
            } catch (e: Exception) {
                println("Servidor offline al actualizar puntuacion: ${e.message}")
            }
            if (!exito) {
                try {
                    pendiente.insertOperacionPendiente(
                        TipoAccion.ACTUALIZACION.getNombreAccion(),
                        Entity.EQUIPO.getNombreEntity(),
                        idEquipo.toString(),
                        "{\"puntos\":$puntos}",
                        getCurrentTimestampSeconds(),
                        0L
                    )
                } catch (e: Exception) {
                    println("Error al registrar operación pendiente: ${e.message}")
                }
            }
        }
    }

    override suspend fun addUsuarioAlEquipo(idEquipo: Long, idUsuario: Long): Boolean = withContext(NonCancellable + Dispatchers.IO) {
        val localSuccess = try {
            local.addUsuarioAlEquipo(idEquipo, idUsuario)
        } catch (e: Exception) {
            println("Error local al añadir usuario al equipo: ${e.message}")
            false
        }

        if (SettingsManager.isUsuarioInvitado()) return@withContext localSuccess

        var remoteSuccess = false
        try {
            remoteSuccess = remote.addUsuarioAlEquipo(idEquipo, idUsuario)
        } catch (e: Exception) {
            println("Error remoto al añadir usuario al equipo: ${e.message}")
        }

        if (!remoteSuccess) {
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.ACTUALIZACION.getNombreAccion(),
                    Entity.EQUIPO.getNombreEntity(),
                    idEquipo.toString(),
                    "{\"idUsuario\":$idUsuario, \"accion\":\"ADD\"}",
                    getCurrentTimestampSeconds(),
                    0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }

        remoteSuccess || localSuccess
    }

    override suspend fun removeUsuarioDelEquipo(idEquipo: Long, idUsuario: Long): Boolean = withContext(NonCancellable + Dispatchers.IO) {
        val localSuccess = try {
            local.removeUsuarioDelEquipo(idEquipo, idUsuario)
        } catch (e: Exception) {
            println("Error local al eliminar usuario del equipo: ${e.message}")
            false
        }

        if (SettingsManager.isUsuarioInvitado()) return@withContext localSuccess

        var remoteSuccess = false
        try {
            remoteSuccess = remote.removeUsuarioDelEquipo(idEquipo, idUsuario)
            if (remoteSuccess) {
                try {
                    val usuarioActualizado = remoteUsuario.getUserById(idUsuario)
                    SettingsManager.setIdEquipoActual(usuarioActualizado.idEquipo)
                    
                    val nuevoEquipo = remote.getEquipoById(usuarioActualizado.idEquipo)
                    local.insertOrUpdateEquipo(nuevoEquipo)
                    local.addUsuarioAlEquipo(nuevoEquipo.idEquipo, idUsuario)
                } catch (e: Exception) {
                    println("Error al sincronizar tras salir del equipo: ${e.message}")
                }
            }
        } catch (e: Exception) {
            println("Error remoto al eliminar usuario del equipo: ${e.message}")
        }

        if (!remoteSuccess) {
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.ACTUALIZACION.getNombreAccion(),
                    Entity.EQUIPO.getNombreEntity(),
                    idEquipo.toString(),
                    "{\"idUsuario\":$idUsuario, \"accion\":\"REMOVE\"}",
                    getCurrentTimestampSeconds(),
                    0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }

        remoteSuccess || localSuccess
    }
}
