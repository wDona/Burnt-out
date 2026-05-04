package dev.wdona.burntout.data.repository

import dev.wdona.burntout.data.datasource.local.OperacionPendienteLocalDataSource
import dev.wdona.burntout.data.datasource.local.OrganizacionLocalDataSource
import dev.wdona.burntout.data.datasource.mapper.OrganizacionMapper
import dev.wdona.burntout.data.datasource.remote.OrganizacionRemoteDataSource
import dev.wdona.burntout.domain.entity.Entity
import dev.wdona.burntout.domain.model.TipoAccion
import dev.wdona.burntout.domain.repository.OrganizacionRepository
import dev.wdona.burntout.shared.domain.Organizacion
import dev.wdona.burntout.shared.utils.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrganizacionRepositoryImpl(
    private val local: OrganizacionLocalDataSource,
    private val remote: OrganizacionRemoteDataSource,
    private val pendiente: OperacionPendienteLocalDataSource
) : OrganizacionRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Default)

    override suspend fun getOrganizacionById(idOrg: Long): Organizacion? = withContext(NonCancellable + Dispatchers.IO) {
        if (!SettingsManager.isUsuarioInvitado()) {
            repositoryScope.launch {
                try {
                    val org = remote.getOrganizacionById(idOrg)
                    if (org != null) local.insertOrUpdateOrganizacion(org)
                } catch (e: Exception) {
                    println("Servidor invitado (getOrganizacionById): ${e.message}")
                }
            }
        }
        try {
            local.getOrganizacionById(idOrg)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getAllOrganizaciones(): List<Organizacion> = withContext(NonCancellable + Dispatchers.IO) {
        if (!SettingsManager.isUsuarioInvitado()) {
            repositoryScope.launch {
                try {
                    val orgs = remote.getAllOrganizaciones()
                    orgs.forEach { local.insertOrUpdateOrganizacion(it) }
                } catch (e: Exception) {
                    println("Servidor invitado (getAllOrganizaciones): ${e.message}")
                }
            }
        }
        try {
            local.getAllOrganizaciones()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun crearOrganizacion(organizacion: Organizacion) {
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                local.crearOrganizacion(organizacion)
                local.insertPreguntasMBI(organizacion.idOrganizacion)
            } catch (e: Exception) {
                println("Error local al crear organización: ${e.message}")
            }
        }
        if (SettingsManager.isUsuarioInvitado()) return
        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            var idRemoto: Long = -1
            try {
                idRemoto = remote.crearOrganizacion(organizacion)
                exito = idRemoto != -1L
            } catch (e: Exception) {
                println("Servidor invitado al crear organización: ${e.message}")
            }
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.CREACION.getNombreAccion(),
                    Entity.ORGANIZACION.getNombreEntity(),
                    if (exito) idRemoto.toString() else "0",
                    OrganizacionMapper.toJson(organizacion),
                    System.currentTimeMillis(),
                    if (exito) 1L else 0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }

    override suspend fun actualizarOrganizacion(organizacion: Organizacion) {
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                local.actualizarOrganizacion(organizacion)
            } catch (e: Exception) {
                println("Error local al actualizar organización: ${e.message}")
            }
        }
        if (SettingsManager.isUsuarioInvitado()) return
        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            try {
                exito = remote.actualizarOrganizacion(organizacion)
            } catch (e: Exception) {
                println("Servidor invitado al actualizar organización: ${e.message}")
            }
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.ACTUALIZACION.getNombreAccion(),
                    Entity.ORGANIZACION.getNombreEntity(),
                    organizacion.idOrganizacion.toString(),
                    OrganizacionMapper.toJson(organizacion),
                    System.currentTimeMillis(),
                    if (exito) 1L else 0L
                )
            } catch (e: Exception) {
                println("Error al registrar operación pendiente: ${e.message}")
            }
        }
    }

    override suspend fun eliminarOrganizacion(idOrg: Long) {
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                local.eliminarOrganizacion(idOrg)
            } catch (e: Exception) {
                println("Error local al eliminar organización: ${e.message}")
            }
        }
        if (SettingsManager.isUsuarioInvitado()) return
        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            try {
                exito = remote.eliminarOrganizacion(idOrg)
            } catch (e: Exception) {
                println("Servidor invitado al eliminar organización: ${e.message}")
            }
            try {
                pendiente.insertOperacionPendiente(
                    TipoAccion.ELIMINACION.getNombreAccion(),
                    Entity.ORGANIZACION.getNombreEntity(),
                    idOrg.toString(),
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