package dev.wdona.burntout.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class Organizacion(
    val idOrganizacion: Long,
    val nombre: String,
    val isDeleted: Boolean = false
)
