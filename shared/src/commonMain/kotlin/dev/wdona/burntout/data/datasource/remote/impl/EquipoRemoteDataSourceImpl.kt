package dev.wdona.burntout.data.datasource.remote.impl

import dev.wdona.burntout.data.api.EquipoApi
import dev.wdona.burntout.data.datasource.remote.EquipoRemoteDataSource
import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.domain.Usuario

class EquipoRemoteDataSourceImpl(private val equipoApi: EquipoApi) : EquipoRemoteDataSource {
    override suspend fun getEquipoById(idEquipo: Long): Equipo {
        return equipoApi.getEquipoById(idEquipo)
    }

    override suspend fun getEquiposByOrg(idOrg: Long): List<Equipo> {
        return equipoApi.getEquiposByOrg(idOrg)
    }

    override suspend fun crearEquipo(equipo: Equipo): Equipo? {
        return try {
            equipoApi.crearEquipo(equipo)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun actualizarEquipo(equipo: Equipo): Boolean {
        return equipoApi.actualizarEquipo(equipo)
    }

    override suspend fun eliminarEquipo(idEquipo: Long): Boolean {
        return equipoApi.eliminarEquipo(idEquipo)
    }

    override suspend fun getMiembrosEquipo(idEquipo: Long): List<Usuario> {
        return equipoApi.getMiembrosEquipo(idEquipo)
    }

    override suspend fun addUsuarioAlEquipo(idEquipo: Long, idUsuario: Long): Boolean {
        return equipoApi.addUsuarioAlEquipo(idEquipo, idUsuario)
    }
}
