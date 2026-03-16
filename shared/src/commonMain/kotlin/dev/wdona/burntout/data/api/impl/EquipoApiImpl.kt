package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.EquipoApi
import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.domain.Usuario

class EquipoApiImpl : EquipoApi {
    override suspend fun getEquipoById(idEquipo: Long): Equipo {
        throw Exception("Error al obtener el equipo del servidor")
    }

    override suspend fun getEquiposByOrg(idOrg: Long): List<Equipo> {
        return emptyList()
    }

    override suspend fun crearEquipo(equipo: Equipo): Boolean {
        return true
    }

    override suspend fun actualizarEquipo(equipo: Equipo): Boolean {
        return true
    }

    override suspend fun eliminarEquipo(idEquipo: Long): Boolean {
        return true
    }

    override suspend fun getMiembrosEquipo(idEquipo: Long): List<Usuario> {
        return emptyList()
    }
}
