package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.AjusteApi
import dev.wdona.burntout.domain.model.Ajuste
import dev.wdona.burntout.shared.network.ApiClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class AjusteApiImpl(private val client: HttpClient = ApiClient.client) : AjusteApi {
    override suspend fun getAjustes(idUsuario: Long): List<Ajuste> =
        client.get("ajustes/$idUsuario").body()

    override suspend fun anadirAjuste(idUsuario: Long, ajuste: Ajuste): Ajuste =
        client.post("ajustes/$idUsuario") { setBody(ajuste) }.body()

    override suspend fun modificarAjuste(idUsuario: Long, ajuste: Ajuste): Ajuste =
        client.put("ajustes/$idUsuario/${ajuste.idAjuste}") { setBody(ajuste) }.body()

    override suspend fun eliminarAjuste(idUsuario: Long, idAjuste: Long) {
        client.delete("ajustes/$idUsuario/$idAjuste")
    }
}