package dev.wdona.burntout.domain.repository

import dev.wdona.burntout.shared.domain.Tablero

interface TableroRepository {
    suspend fun getTablerosByEquipo(idOrg: Long, idEquipo: Long) : List<Tablero>
    suspend fun getTableroById(idTablero: String) : Tablero
    suspend fun crearTablero(tablero: Tablero)
    suspend fun actualizarTablero(tablero: Tablero)
    suspend fun eliminarTablero(idTablero: String)
    suspend fun tableroExisteRemoto(idTablero: String): Boolean
}
