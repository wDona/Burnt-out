package dev.wdona.burntout.domain.repository

import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.domain.Usuario

interface EquipoRepository {
    suspend fun getEquiposByOrg(idOrg: Long): List<Equipo>
    suspend fun getEquipoById(idEquipo: Long): Equipo?
    suspend fun crearEquipo(equipo: Equipo): Long
    suspend fun actualizarEquipo(equipo: Equipo)
    suspend fun eliminarEquipo(idEquipo: Long)
    suspend fun updatePuntuacion(idEquipo: Long, puntos: Long)
    suspend fun addUsuarioAlEquipo(idEquipo: Long, idUsuario: Long): Boolean
    suspend fun removeUsuarioDelEquipo(idEquipo: Long, idUsuario: Long): Boolean
}
