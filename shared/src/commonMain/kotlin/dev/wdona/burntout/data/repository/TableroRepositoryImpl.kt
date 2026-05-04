package dev.wdona.burntout.data.repository

import dev.wdona.burntout.data.datasource.local.OperacionPendienteLocalDataSource
import dev.wdona.burntout.data.datasource.local.TableroLocalDataSource
import dev.wdona.burntout.data.datasource.mapper.TableroMapper
import dev.wdona.burntout.data.datasource.remote.TableroRemoteDataSource
import dev.wdona.burntout.domain.entity.Entity
import dev.wdona.burntout.domain.model.TipoAccion
import dev.wdona.burntout.domain.repository.TableroRepository
import dev.wdona.burntout.shared.domain.Tablero
import dev.wdona.burntout.shared.utils.SettingsManager
import dev.wdona.burntout.shared.utils.getCurrentTimestampSeconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class TableroRepositoryImpl(
    private val local: TableroLocalDataSource,
    private val remote: TableroRemoteDataSource,
    private val pendiente: OperacionPendienteLocalDataSource
) : TableroRepository {

    private val mutex = Mutex()

    override suspend fun getTablerosByEquipo(idOrg: Long, idEquipo: Long): List<Tablero> = withContext(NonCancellable + Dispatchers.IO) {
        mutex.withLock {
            if (!SettingsManager.isUsuarioInvitado()) {
                try {
                    val tableros = remote.getTablerosByOrg(idOrg, idEquipo)
                    tableros.forEach { local.insertOrUpdateTablero(it) }
                } catch (e: Exception) {
                    println("Servidor offline (getTablerosByOrg): ${e.message}")
                }
            }
            local.getTablerosByOrg(idOrg).filter { it.idEquipo == null || it.idEquipo == idEquipo }
        }
    }

    override suspend fun getTableroById(idTablero: String): Tablero = withContext(NonCancellable + Dispatchers.IO) {
        mutex.withLock {
            if (!SettingsManager.isUsuarioInvitado()) {
                try {
                    val tablero = remote.getTableroById(idTablero)
                    local.insertOrUpdateTablero(tablero)
                } catch (e: Exception) {
                    println("Servidor offline (getTableroById): ${e.message}")
                }
            }
            local.getTableroById(idTablero)
        }
    }

    override suspend fun crearTablero(tablero: Tablero) {
        withContext(NonCancellable + Dispatchers.IO) {
            mutex.withLock {
                try {
                    local.crearTablero(tablero)
                } catch (e: Exception) {
                    println("Error local al crear tablero: ${e.message}")
                }

                if (SettingsManager.isUsuarioInvitado()) return@withLock

                var exitoRemoto = false
                try {
                    exitoRemoto = remote.crearTablero(tablero).isNotEmpty()
                } catch (e: Exception) {
                    println("Servidor offline al crear tablero: ${e.message}")
                }

                try {
                    pendiente.insertOperacionPendiente(
                        TipoAccion.CREACION.getNombreAccion(),
                        Entity.TABLERO.getNombreEntity(),
                        tablero.idTablero,
                        TableroMapper.toJson(tablero),
                        System.currentTimeMillis(),
                        if (exitoRemoto) 1L else 0L
                    )
                } catch (e: Exception) {
                    println("Error al registrar operación pendiente: ${e.message}")
                }
            }
        }
    }

    override suspend fun actualizarTablero(tablero: Tablero) {
        withContext(NonCancellable + Dispatchers.IO) {
            mutex.withLock {
                try {
                    local.actualizarTablero(tablero)
                } catch (e: Exception) {
                    println("Error local al actualizar tablero: ${e.message}")
                }

                if (SettingsManager.isUsuarioInvitado()) return@withLock

                var exito = false
                try {
                    exito = remote.actualizarTablero(tablero)
                } catch (e: Exception) {
                    println("Servidor offline al actualizar tablero: ${e.message}")
                }
                try {
                    pendiente.insertOperacionPendiente(
                        TipoAccion.ACTUALIZACION.getNombreAccion(),
                        Entity.TABLERO.getNombreEntity(),
                        tablero.idTablero,
                        TableroMapper.toJson(tablero),
                        getCurrentTimestampSeconds(),
                        if (exito) 1L else 0L
                    )
                } catch (e: Exception) {
                    println("Error al registrar operación pendiente: ${e.message}")
                }
            }
        }
    }

    override suspend fun eliminarTablero(idTablero: String) {
        withContext(NonCancellable + Dispatchers.IO) {
            mutex.withLock {
                try {
                    local.eliminarTablero(idTablero)
                } catch (e: Exception) {
                    println("Error local al eliminar tablero: ${e.message}")
                }

                if (SettingsManager.isUsuarioInvitado()) return@withLock

                var exito = false
                try {
                    exito = remote.eliminarTablero(idTablero)
                } catch (e: Exception) {
                    println("Servidor offline al eliminar tablero: ${e.message}")
                }
                try {
                    pendiente.insertOperacionPendiente(
                        TipoAccion.ELIMINACION.getNombreAccion(),
                        Entity.TABLERO.getNombreEntity(),
                        idTablero,
                        "",
                        getCurrentTimestampSeconds(),
                        if (exito) 1L else 0L
                    )
                } catch (e: Exception) {
                    println("Error al registrar operación pendiente: ${e.message}")
                }
            }
        }
    }
}
