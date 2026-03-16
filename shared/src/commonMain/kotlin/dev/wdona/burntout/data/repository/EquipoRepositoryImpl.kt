package dev.wdona.burntout.data.repository

import dev.wdona.burntout.data.datasource.local.EquipoLocalDataSource
import dev.wdona.burntout.data.datasource.local.OperacionPendienteLocalDataSource
import dev.wdona.burntout.data.datasource.local.UsuarioLocalDataSource
import dev.wdona.burntout.data.datasource.mapper.EquipoMapper
import dev.wdona.burntout.data.datasource.remote.EquipoRemoteDataSource
import dev.wdona.burntout.domain.entity.Entity
import dev.wdona.burntout.domain.model.TipoAccion
import dev.wdona.burntout.domain.repository.EquipoRepository
import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.domain.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EquipoRepositoryImpl(
    private val local: EquipoLocalDataSource,
    private val remote: EquipoRemoteDataSource,
    private val pendiente: OperacionPendienteLocalDataSource,
) : EquipoRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Default)

    override suspend fun getEquiposByOrg(idOrg: Long): List<Equipo> = withContext(Dispatchers.IO) {
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
        try {
            local.getEquiposByOrg(idOrg)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getEquipoById(idEquipo: Long): Equipo? = withContext(Dispatchers.IO) {
        repositoryScope.launch {
            try {
                val equipoRemoto = remote.getEquipoById(idEquipo)
                local.insertOrUpdateEquipo(equipoRemoto)
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

    override suspend fun crearEquipo(equipo: Equipo) {
        val idGenerado = withContext(Dispatchers.IO) {
            try {
                local.crearEquipo(equipo)
            } catch (e: Exception) {
                -1L
            }
        }

        repositoryScope.launch {
            var exito = false
            try {
                exito = remote.crearEquipo(equipo)
            } catch (e: Exception) {
                // Servidor offline
            }

            withContext(Dispatchers.IO) {
                pendiente.insertOperacionPendiente(
                    TipoAccion.CREACION.getNombreAccion(),
                    Entity.EQUIPO.getNombreEntity(),
                    if (exito) equipo.idEquipo else idGenerado,
                    EquipoMapper.toJson(equipo),
                    System.currentTimeMillis(),
                    if (exito) 1L else 0L
                )
            }
        }
    }

    override suspend fun actualizarEquipo(equipo: Equipo) {
        withContext(Dispatchers.IO) {
            local.actualizarEquipo(equipo)
        }

        repositoryScope.launch {
            var exito = false
            try {
                exito = remote.actualizarEquipo(equipo)
            } catch (e: Exception) {}

            withContext(Dispatchers.IO) {
                pendiente.insertOperacionPendiente(
                    TipoAccion.ACTUALIZACION.getNombreAccion(),
                    Entity.EQUIPO.getNombreEntity(),
                    equipo.idEquipo,
                    EquipoMapper.toJson(equipo),
                    System.currentTimeMillis(),
                    if (exito) 1L else 0L
                )
            }
        }
    }

    override suspend fun eliminarEquipo(idEquipo: Long) {
        withContext(Dispatchers.IO) {
            local.eliminarEquipo(idEquipo)
        }

        repositoryScope.launch {
            var exito = false
            try {
                exito = remote.eliminarEquipo(idEquipo)
            } catch (e: Exception) {}

            withContext(Dispatchers.IO) {
                pendiente.insertOperacionPendiente(
                    TipoAccion.ELIMINACION.getNombreAccion(),
                    Entity.EQUIPO.getNombreEntity(),
                    idEquipo,
                    "",
                    System.currentTimeMillis(),
                    if (exito) 1L else 0L
                )
            }
        }
    }



    override suspend fun updatePuntuacion(idEquipo: Long, puntos: Long) {
        withContext(Dispatchers.IO) {
            local.updatePuntuacion(idEquipo, puntos)
        }
    }
}
