package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.TableroApi
import dev.wdona.burntout.shared.domain.Tablero
import dev.wdona.burntout.shared.network.ApiClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse

class TableroApiImpl(private val client: HttpClient = ApiClient.client) : TableroApi {
    override suspend fun getTableroById(idTablero: Long): Tablero {
        return client.get("/tablero/$idTablero").body()
    }

    override suspend fun getTablerosByOrg(idOrg: Long): List<Tablero> {
        return client.get("/$idOrg/tableros").body()
    }

    override suspend fun crearTablero(tablero: Tablero): HttpResponse {
        val response = client.post("/nuevo/tablero/titulo=${tablero.titulo}&idOrganizacion=${tablero.idOrganizacion}")
        return response
    }

    override suspend fun actualizarTablero(tablero: Tablero): HttpResponse {
        val response = client.post("/actualizar/tablero/idTablero=${tablero.idTablero}&titulo=${tablero.titulo}&idOrganizacion=${tablero.idOrganizacion}")
        return response
    }

    override suspend fun eliminarTablero(idTablero: Long): HttpResponse {
        val response = client.post("/eliminar/tablero/idTablero=$idTablero")
        return response
    }
}
