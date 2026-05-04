package dev.wdona.burntout.data.datasource.remote

import dev.wdona.burntout.shared.domain.Tablero

interface TableroRemoteDataSource {
    suspend fun getTableroById(idTablero: String): Tablero
    suspend fun getTablerosByOrg(idOrg: Long, idEquipo: Long): List<Tablero>
    suspend fun crearTablero(tablero: Tablero): String
    suspend fun actualizarTablero(tablero: Tablero): Boolean
    suspend fun eliminarTablero(idTablero: String): Boolean
}
