package dev.wdona.burntout.data.repository

import dev.wdona.burntout.data.datasource.local.AjusteLocalDataSource
import dev.wdona.burntout.data.datasource.local.OperacionPendienteLocalDataSource
import dev.wdona.burntout.data.datasource.local.UsuarioLocalDataSource
import dev.wdona.burntout.data.datasource.remote.AjusteRemoteDataSource
import dev.wdona.burntout.domain.entity.Entity
import dev.wdona.burntout.domain.model.Ajuste
import dev.wdona.burntout.domain.repository.AjusteRepository
import dev.wdona.burntout.domain.model.TipoAccion
import dev.wdona.burntout.data.datasource.mapper.AjusteMapper
import dev.wdona.burntout.shared.utils.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AjusteRepositoryImpl(
    private val localDataSource: AjusteLocalDataSource,
    private val remoteDataSource: AjusteRemoteDataSource,
    private val equipoRemote: dev.wdona.burntout.data.datasource.remote.EquipoRemoteDataSource,
    private val usuarioRemote: dev.wdona.burntout.data.datasource.remote.UsuarioRemoteDataSource,
    private val equipoLocal: dev.wdona.burntout.data.datasource.local.EquipoLocalDataSource,
    private val operacionesPendientesDatasource: OperacionPendienteLocalDataSource
) : AjusteRepository {

    override suspend fun salirDelEquipo(idEquipo: Long, idUsuario: Long): Boolean = withContext(NonCancellable + Dispatchers.IO) {
        try {
            equipoLocal.removeUsuarioDelEquipo(idEquipo, idUsuario)
        } catch (e: Exception) {
            println("Error local al salir del equipo: ${e.message}")
        }

        if (SettingsManager.isUsuarioInvitado()) return@withContext true

        var exito = false
        try {
            exito = equipoRemote.removeUsuarioDelEquipo(idEquipo, idUsuario)
            if (exito) {
                try {
                    val usuarioActualizado = usuarioRemote.getUserById(idUsuario)
                    SettingsManager.setIdEquipoActual(usuarioActualizado.idEquipo)
                    
                    val nuevoEquipo = equipoRemote.getEquipoById(usuarioActualizado.idEquipo)
                    equipoLocal.insertOrUpdateEquipo(nuevoEquipo)
                    equipoLocal.addUsuarioAlEquipo(nuevoEquipo.idEquipo, idUsuario)
                } catch (e: Exception) {
                    println("Error al sincronizar tras salir del equipo: ${e.message}")
                }
            }
        } catch (e: Exception) {
            println("Error remoto al salir del equipo: ${e.message}")
        }
        
        exito
    }

    override suspend fun modificarAjuste(idUsuario: Long, ajuste: Ajuste) {
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                localDataSource.modificarAjuste(idUsuario, ajuste)
            } catch (e: Exception) {
                println("Error al intentar modificar ajuste en local: ${e.message}")
            }
        }

        if (SettingsManager.isUsuarioInvitado()) return
        withContext(NonCancellable + Dispatchers.IO) {
            var isSincronizado = false
            try {
                remoteDataSource.modificarAjuste(idUsuario, ajuste)
                isSincronizado = true
            } catch (e: Exception) {
                println("No se ha podido modificar ajuste remoto: ${e.message}")
            }

            try {
                operacionesPendientesDatasource.insertOperacionPendiente(
                    TipoAccion.ACTUALIZACION.getNombreAccion(),
                    Entity.AJUSTE.getNombreEntity(),
                    "" + ajuste.idAjuste,
                    AjusteMapper.toJson(ajuste),
                    System.currentTimeMillis(),
                    if (isSincronizado) 1L else 0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }

    override suspend fun getAjustesByUsuario(idUsuario: Long): List<Ajuste> = withContext(NonCancellable + Dispatchers.IO) {
        if (idUsuario == Long.MIN_VALUE) {
            return@withContext localDataSource.getAjustesByUsuario(idUsuario)
        }

        CoroutineScope(Dispatchers.Default).launch {
            try {
                val ajustesRemotos = remoteDataSource.getAjustesByUsuario(idUsuario)
                ajustesRemotos.forEach { localDataSource.modificarAjuste(idUsuario, it) }
            } catch (e: Exception) {
                println("Servidor invitado (getAjustesByUsuario): ${e.message}")
            }
        }

        try {
            localDataSource.getAjustesByUsuario(idUsuario)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getAjusteByIdYUsuario(
        idAjuste: Long,
        idUsuario: Long
    ): Ajuste = withContext(NonCancellable + Dispatchers.IO) {
        if (idUsuario == Long.MIN_VALUE) {
            return@withContext localDataSource.getAjusteByIdYUsuario(idAjuste, idUsuario)
        }

        CoroutineScope(Dispatchers.Default).launch {
            try {
                val ajusteRemoto = remoteDataSource.getAjusteByIdYUsuario(idAjuste, idUsuario)
                localDataSource.modificarAjuste(idUsuario, ajusteRemoto)
            } catch (e: Exception) {
                println("Servidor invitado (getAjusteByIdYUsuario): ${e.message}")
            }
        }

        localDataSource.getAjusteByIdYUsuario(idAjuste, idUsuario)
    }

}