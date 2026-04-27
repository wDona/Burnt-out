package dev.wdona.burntout.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class RegistroRequest(
    val username: String,
    val password: String,
    val nombre: String,
    val modo: String,            // "CREAR_ORG" | "UNIRSE"
    val nombreOrg: String? = null,
    val codigoInvitacion: String? = null
)
