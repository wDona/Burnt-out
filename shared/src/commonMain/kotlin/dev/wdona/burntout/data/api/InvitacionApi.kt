package dev.wdona.burntout.data.api

import dev.wdona.burntout.shared.domain.GenerarInvitacionRequest
import dev.wdona.burntout.shared.domain.InvitacionCode

interface InvitacionApi {
    suspend fun generarCodigo(request: GenerarInvitacionRequest): InvitacionCode
    suspend fun listarCodigos(idOrg: Long, idUsuarioAdmin: Long): List<InvitacionCode>
}
