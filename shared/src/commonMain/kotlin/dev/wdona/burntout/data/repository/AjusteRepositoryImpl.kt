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
    private val pendienteDataSource: OperacionPendienteLocalDataSource
) : AjusteRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Default)

    override suspend fun modificarAjuste(ajuste: Ajuste) {
        withContext(Dispatchers.IO) {
            try {
                localDataSource.modificarAjuste(ajuste)
            } catch (e: Exception) {
                println("Error local al modificar ajuste: ${e.message}")
            }
        }
        if (SettingsManager.isUsuarioInvitado()) return
        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            try {
                remoteDataSource.modificarAjuste(ajuste)
                exito = true
            } catch (e: Exception) {
                println("Servidor offline al modificar ajuste: ${e.message}")
            }
            try {
                pendienteDataSource.insertOperacionPendiente(
                    TipoAccion.ACTUALIZACION.getNombreAccion(),
                    Entity.AJUSTE.getNombreEntity(),
                    ajuste.idAjuste,
                    AjusteMapper.toJson(ajuste),
                    System.currentTimeMillis(),
                    if (exito) 1L else 0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }

    override suspend fun getAjustesByUsuario(idUsuario: Long): List<Ajuste> = withContext(Dispatchers.IO) {
        if (idUsuario == Long.MIN_VALUE) {
            return@withContext localDataSource.getAjustesByUsuario(idUsuario)
        }

        repositoryScope.launch {
            try {
                val ajustesRemotos = remoteDataSource.getAjustesByUsuario(idUsuario)
                ajustesRemotos.forEach { localDataSource.modificarAjuste(it) }
            } catch (e: Exception) {
                println("Servidor offline (getAjustesByUsuario): ${e.message}")
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
    ): Ajuste = withContext(Dispatchers.IO) {
        if (idUsuario == Long.MIN_VALUE) {
            return@withContext localDataSource.getAjusteByIdYUsuario(idAjuste, idUsuario)
        }

        repositoryScope.launch {
            try {
                val ajusteRemoto = remoteDataSource.getAjusteByIdYUsuario(idAjuste, idUsuario)
                localDataSource.modificarAjuste(ajusteRemoto)
            } catch (e: Exception) {
                println("Servidor offline (getAjusteByIdYUsuario): ${e.message}")
            }
        }

        localDataSource.getAjusteByIdYUsuario(idAjuste, idUsuario)
    }

}