package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.TableroApi
import dev.wdona.burntout.shared.domain.Tablero
import dev.wdona.burntout.shared.network.ApiClient
import dev.wdona.burntout.shared.utils.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.statement.HttpResponse

class TableroApiImpl(private val client: HttpClient = ApiClient.client) : TableroApi {
    private val TAG = "TableroApiImpl"

    override suspend fun getTableroById(idTablero: Long): Tablero {
        Logger.d(TAG, "getTableroById: $idTablero")
        return client.get("tableros/$idTablero").body()
    }

    override suspend fun getTablerosByOrg(idOrg: Long): List<Tablero> {
        Logger.d(TAG, "getTablerosByOrg: $idOrg")
        return client.get("tableros?idOrg=$idOrg").body()
    }

    override suspend fun crearTablero(tablero: Tablero): HttpResponse {
        Logger.d(TAG, "crearTablero: $tablero")
        return client.post("tableros") {
            contentType(ContentType.Application.Json)
            setBody(tablero)
        }
    }

    override suspend fun actualizarTablero(tablero: Tablero): HttpResponse {
        Logger.d(TAG, "actualizarTablero: $tablero")
        return client.put("tableros/${tablero.idTablero}") {
            contentType(ContentType.Application.Json)
            setBody(tablero)
        }
    }

    override suspend fun eliminarTablero(idTablero: Long): HttpResponse {
        Logger.d(TAG, "eliminarTablero: $idTablero")
        return client.delete("tableros/$idTablero")
    }
}