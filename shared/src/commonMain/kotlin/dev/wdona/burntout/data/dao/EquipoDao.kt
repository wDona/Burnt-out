package dev.wdona.burntout.data.dao

import dev.wdona.burntout.shared.domain.Equipo

interface EquipoDao {
    suspend fun getEquipoById(idEquipo: Long): Equipo
    suspend fun getEquiposByOrg(idOrg: Long): List<Equipo>
    suspend fun crearEquipo(equipo: Equipo): Long
    suspend fun actualizarEquipo(equipo: Equipo): Boolean
    suspend fun eliminarEquipo(idEquipo: Long): Boolean
    suspend fun insertOrUpdateEquipo(equipo: Equipo): Boolean
    suspend fun updatePuntuacion(idEquipo: Long, puntos: Long): Boolean
    suspend fun addUsuarioAlEquipo(idEquipo: Long, idUsuario: Long): Boolean
}
