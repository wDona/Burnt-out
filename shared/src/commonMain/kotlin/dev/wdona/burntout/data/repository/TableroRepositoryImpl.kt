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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class TableroRepositoryImpl(
    private val local: TableroLocalDataSource,
    private val remote: TableroRemoteDataSource,
    private val pendiente: OperacionPendienteLocalDataSource
) : TableroRepository {

    override suspend fun getTablerosByOrg(idOrg: Long): List<Tablero> = withContext(Dispatchers.IO) {
        if (!SettingsManager.isUsuarioInvitado()) {
            try {
                val tableros = remote.getTablerosByOrg(idOrg)
                local.eliminarTablerosPorOrg(idOrg)
                tableros.forEach { local.insertOrUpdateTablero(it) }
            } catch (e: Exception) {
                println("Servidor invitado (getTablerosByOrg): ${e.message}")
            }
        }
        local.getTablerosByOrg(idOrg)
    }

    override suspend fun getTableroById(idTablero: Long): Tablero? = withContext(Dispatchers.IO) {
        if (!SettingsManager.isUsuarioInvitado()) {
            try {
                val tablero = remote.getTableroById(idTablero)
                local.insertOrUpdateTablero(tablero)
            } catch (e: Exception) {
                println("Servidor invitado (getTableroById): ${e.message}")
            }
        }
        local.getTableroById(idTablero)
    }

    override suspend fun crearTablero(tablero: Tablero) {
        withContext(Dispatchers.IO) {
            try {
                local.crearTablero(tablero)
            } catch (e: Exception) {
                println("Error local al crear tablero: ${e.message}")
            }
        }

        if (SettingsManager.isUsuarioInvitado()) return

        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            try {
                exito = remote.crearTablero(tablero)
            } catch (e: Exception) {
                println("Servidor invitado al crear tablero: ${e.message}")
            }
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.CREACION.getNombreAccion(),
                    Entity.TABLERO.getNombreEntity(),
                    tablero.idTablero,
                    TableroMapper.toJson(tablero),
                    System.currentTimeMillis(),
                    if (exito) 1L else 0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }

    override suspend fun actualizarTablero(tablero: Tablero) {
        withContext(Dispatchers.IO) {
            try {
                local.actualizarTablero(tablero)
            } catch (e: Exception) {
                println("Error local al actualizar tablero: ${e.message}")
            }
        }

        if (SettingsManager.isUsuarioInvitado()) return

        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            try {
                exito = remote.actualizarTablero(tablero)
            } catch (e: Exception) {
                println("Servidor invitado al actualizar tablero: ${e.message}")
            }
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.ACTUALIZACION.getNombreAccion(),
                    Entity.TABLERO.getNombreEntity(),
                    tablero.idTablero,
                    TableroMapper.toJson(tablero),
                    System.currentTimeMillis(),
                    if (exito) 1L else 0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }

    override suspend fun eliminarTablero(idTablero: Long) {
        withContext(Dispatchers.IO) {
            try {
                local.eliminarTablero(idTablero)
            } catch (e: Exception) {
                println("Error local al eliminar tablero: ${e.message}")
            }
        }

        if (SettingsManager.isUsuarioInvitado()) return

        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            try {
                exito = remote.eliminarTablero(idTablero)
            } catch (e: Exception) {
                println("Servidor invitado al eliminar tablero: ${e.message}")
            }
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.ELIMINACION.getNombreAccion(),
                    Entity.TABLERO.getNombreEntity(),
                    idTablero,
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
