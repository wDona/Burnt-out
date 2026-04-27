package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.InvitacionApi
import dev.wdona.burntout.shared.domain.GenerarInvitacionRequest
import dev.wdona.burntout.shared.domain.InvitacionCode
import dev.wdona.burntout.shared.network.ApiClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class InvitacionApiImpl(private val client: HttpClient = ApiClient.client) : InvitacionApi {
    override suspend fun generarCodigo(request: GenerarInvitacionRequest): InvitacionCode =
        client.post("invitaciones") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun listarCodigos(idOrg: Long, idUsuarioAdmin: Long): List<InvitacionCode> =
        client.get("invitaciones?idOrg=$idOrg&idUsuarioAdmin=$idUsuarioAdmin").body()
}
