package dev.wdona.burntout.data.datasource.remote.impl

import dev.wdona.burntout.data.api.TableroApi
import dev.wdona.burntout.data.datasource.remote.TableroRemoteDataSource
import dev.wdona.burntout.shared.domain.Tablero
import io.ktor.client.call.body

class TableroRemoteDataSourceImpl(private val tableroApi: TableroApi) : TableroRemoteDataSource {
    override suspend fun getTableroById(idTablero: String): Tablero {
        return tableroApi.getTableroById(idTablero)
    }

    override suspend fun getTablerosByOrg(idOrg: Long, idEquipo: Long): List<Tablero> {
        return tableroApi.getTablerosByOrg(idOrg, idEquipo)
    }

    override suspend fun crearTablero(tablero: Tablero): String {
        val response = tableroApi.crearTablero(tablero)
        return if (response.status.value in 200..299) tablero.idTablero else ""
    }

    override suspend fun actualizarTablero(tablero: Tablero): Boolean {
        return tableroApi.actualizarTablero(tablero).status.value in 200..299
    }

    override suspend fun eliminarTablero(idTablero: String): Boolean {
        val status = tableroApi.eliminarTablero(idTablero).status.value
        return status in 200..299 || status == 404
    }

    override suspend fun tableroExisteRemoto(idTablero: String): Boolean {
        return tableroApi.tableroExisteRemoto(idTablero)
    }
}
