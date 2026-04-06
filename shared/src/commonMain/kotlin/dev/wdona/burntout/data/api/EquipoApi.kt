package dev.wdona.burntout.data.api

import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.domain.Usuario

interface EquipoApi {
    suspend fun getEquipoById(idEquipo: Long): Equipo
    suspend fun getEquiposByOrg(idOrg: Long): List<Equipo>
    suspend fun crearEquipo(equipo: Equipo): Equipo
    suspend fun actualizarEquipo(equipo: Equipo): Boolean
    suspend fun eliminarEquipo(idEquipo: Long): Boolean
    suspend fun getMiembrosEquipo(idEquipo: Long): List<Usuario>
    suspend fun addUsuarioAlEquipo(idEquipo: Long, idUsuario: Long): Boolean
    suspend fun removeUsuarioDelEquipo(idEquipo: Long, idUsuario: Long): Boolean
}
