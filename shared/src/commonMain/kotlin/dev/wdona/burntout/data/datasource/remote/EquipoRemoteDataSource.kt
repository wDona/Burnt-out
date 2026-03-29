package dev.wdona.burntout.data.datasource.remote

import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.domain.Usuario

interface EquipoRemoteDataSource {
    suspend fun getEquipoById(idEquipo: Long): Equipo
    suspend fun getEquiposByOrg(idOrg: Long): List<Equipo>
    suspend fun crearEquipo(equipo: Equipo): Boolean
    suspend fun actualizarEquipo(equipo: Equipo): Boolean
    suspend fun eliminarEquipo(idEquipo: Long): Boolean
    suspend fun getMiembrosEquipo(idEquipo: Long): List<Usuario>
    suspend fun addUsuarioAlEquipo(idEquipo: Long, idUsuario: Long): Boolean
}
