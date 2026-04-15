package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.AjusteApi
import dev.wdona.burntout.domain.model.Ajuste
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

class AjusteApiImpl(private val client: HttpClient = ApiClient.client) : AjusteApi {
    private val TAG = "AjusteApiImpl"

    override suspend fun getAjustes(idUsuario: Long): List<Ajuste> {
        Logger.d(TAG, "getAjustes: user=$idUsuario")
        return client.get("ajustes/$idUsuario").body()
    }

    override suspend fun anadirAjuste(idUsuario: Long, ajuste: Ajuste): Ajuste {
        Logger.d(TAG, "anadirAjuste: user=$idUsuario, ajuste=$ajuste")
        return client.post("ajustes/$idUsuario") {
            contentType(ContentType.Application.Json)
            setBody(ajuste)
        }.body()
    }

    override suspend fun modificarAjuste(idUsuario: Long, ajuste: Ajuste): Ajuste {
        Logger.d(TAG, "modificarAjuste: user=$idUsuario, ajuste=$ajuste")
        return client.put("ajustes/$idUsuario/${ajuste.idAjuste}") {
            contentType(ContentType.Application.Json)
            setBody(ajuste)
        }.body()
    }

    override suspend fun eliminarAjuste(idUsuario: Long, idAjuste: Long) {
        Logger.d(TAG, "eliminarAjuste: user=$idUsuario, ajuste=$idAjuste")
        client.delete("ajustes/$idUsuario/$idAjuste")
    }
}