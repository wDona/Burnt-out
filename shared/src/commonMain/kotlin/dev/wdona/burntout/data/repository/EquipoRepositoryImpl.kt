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
        withContext(Dispatchers.IO) {
            try {
                local.crearEquipo(equipo)
            } catch (e: Exception) {
                println("Error local al crear equipo: ${e.message}")
            }
        }

        repositoryScope.launch {
            var exito = false
            try {
                exito = remote.crearEquipo(equipo)
            } catch (e: Exception) {
                println("Servidor offline al crear equipo: ${e.message}")
            }

            withContext(Dispatchers.IO) {
                try {
                    pendiente.insertOperacionPendiente(
                        TipoAccion.CREACION.getNombreAccion(),
                        Entity.EQUIPO.getNombreEntity(),
                        equipo.idEquipo,
                        EquipoMapper.toJson(equipo),
                        System.currentTimeMillis(),
                        if (exito) 1L else 0L
                    )
                } catch (e: Exception) {
                    println("Error al registrar operación pendiente: ${e.message}")
                }
            }
        }
    }

    override suspend fun actualizarEquipo(equipo: Equipo) {
        withContext(Dispatchers.IO) {
            try {
                local.actualizarEquipo(equipo)
            } catch (e: Exception) {
                println("Error local al actualizar equipo: ${e.message}")
            }
        }

        repositoryScope.launch {
            var exito = false
            try {
                exito = remote.actualizarEquipo(equipo)
            } catch (e: Exception) {
                println("Servidor offline al actualizar equipo: ${e.message}")
            }

            withContext(Dispatchers.IO) {
                try {
                    pendiente.insertOperacionPendiente(
                        TipoAccion.ACTUALIZACION.getNombreAccion(),
                        Entity.EQUIPO.getNombreEntity(),
                        equipo.idEquipo,
                        EquipoMapper.toJson(equipo),
                        System.currentTimeMillis(),
                        if (exito) 1L else 0L
                    )
                } catch (e: Exception) {
                    println("Error al registrar operación pendiente: ${e.message}")
                }
            }
        }
    }

    override suspend fun eliminarEquipo(idEquipo: Long) {
        withContext(Dispatchers.IO) {
            try {
                local.eliminarEquipo(idEquipo)
            } catch (e: Exception) {
                println("Error local al eliminar equipo: ${e.message}")
            }
        }

        repositoryScope.launch {
            var exito = false
            try {
                exito = remote.eliminarEquipo(idEquipo)
            } catch (e: Exception) {
                println("Servidor offline al eliminar equipo: ${e.message}")
            }

            withContext(Dispatchers.IO) {
                try {
                    pendiente.insertOperacionPendiente(
                        TipoAccion.ELIMINACION.getNombreAccion(),
                        Entity.EQUIPO.getNombreEntity(),
                        idEquipo,
                        "",
                        System.currentTimeMillis(),
                        if (exito) 1L else 0L
                    )
                } catch (e: Exception) {
                    println("Error al registrar operación pendiente: ${e.message}")
                }
            }
        }
    }

    override suspend fun updatePuntuacion(idEquipo: Long, puntos: Long) {
        withContext(Dispatchers.IO) {
            local.updatePuntuacion(idEquipo, puntos)
        }
    }
}
