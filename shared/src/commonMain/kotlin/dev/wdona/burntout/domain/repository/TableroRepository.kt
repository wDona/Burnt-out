package dev.wdona.burntout.domain.repository

import dev.wdona.burntout.shared.domain.Tablero

interface TableroRepository {
    suspend fun getTablerosByOrg(idOrg: Long) : List<Tablero>
    suspend fun getTableroById(idTablero: Long) : Tablero?
    suspend fun crearTablero(tablero: Tablero)
    suspend fun actualizarTablero(tablero: Tablero)
    suspend fun eliminarTablero(idTablero: Long)
}
