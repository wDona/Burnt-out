package dev.wdona.burntout.data.datasource.local

import dev.wdona.burntout.shared.domain.Tablero

interface TableroLocalDataSource {
    suspend fun getTableroById(idTablero: Long): Tablero
    suspend fun getTablerosByOrg(idOrg: Long): List<Tablero>
    suspend fun crearTablero(tablero: Tablero): Long
    suspend fun actualizarTablero(tablero: Tablero): Boolean
    suspend fun eliminarTablero(idTablero: Long): Boolean
    suspend fun insertOrUpdateTablero(tablero: Tablero): Boolean
    suspend fun eliminarTablerosPorOrg(idOrg: Long)
}
