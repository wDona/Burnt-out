package dev.wdona.burntout.data.datasource.remote.impl

import dev.wdona.burntout.data.api.TableroApi
import dev.wdona.burntout.data.datasource.remote.TableroRemoteDataSource
import dev.wdona.burntout.shared.domain.Tablero

class TableroRemoteDataSourceImpl(private val tableroApi: TableroApi) : TableroRemoteDataSource {
    override suspend fun getTableroById(idTablero: Long): Tablero {
        return tableroApi.getTableroById(idTablero)
    }

    override suspend fun getTablerosByOrg(idOrg: Long, idEquipo: Long): List<Tablero> {
        return tableroApi.getTablerosByOrg(idOrg, idEquipo)
    }

    override suspend fun crearTablero(tablero: Tablero): Boolean {
        return tableroApi.crearTablero(tablero).status.value in 200..299
    }

    override suspend fun actualizarTablero(tablero: Tablero): Boolean {
        return tableroApi.actualizarTablero(tablero).status.value in 200..299
    }

    override suspend fun eliminarTablero(idTablero: Long): Boolean {
        return tableroApi.eliminarTablero(idTablero).status.value in 200..299
    }
}
