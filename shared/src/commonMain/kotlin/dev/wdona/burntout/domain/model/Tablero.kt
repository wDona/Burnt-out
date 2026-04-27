package dev.wdona.burntout.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class Tablero(
    val idTablero: Long,
    val titulo: String,
    val idOrganizacion: Long,
    val idEquipo: Long?,
    val isDeleted: Boolean = false
) // Id equipo anadido para saber pertenencia
