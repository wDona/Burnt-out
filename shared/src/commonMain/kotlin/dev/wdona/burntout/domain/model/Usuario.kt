package dev.wdona.burntout.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val idUsuario: Long,
    val username: String,
    val password: String,
    val nombre: String,
    val riesgoBurnout: Double?,
    val descripcion: String?,
    val idOrganizacion: Long,
    val idEquipo: Long,
    val rol: String = "MEMBER"
)
