package dev.wdona.burntout.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class InvitacionCode(
    val code: String,
    val idOrganizacion: Long,
    val rol: String,
    val creadoPor: Long,
    val creadoEn: Long,
    val expiraEn: Long? = null,
    val usadoEn: Long? = null,
    val usadoPor: Long? = null
)

@Serializable
data class GenerarInvitacionRequest(
    val idUsuarioAdmin: Long,
    val rol: String = "MEMBER",
    val expiraEn: Long? = null
)
