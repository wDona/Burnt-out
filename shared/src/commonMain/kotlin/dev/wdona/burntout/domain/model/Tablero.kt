package dev.wdona.burntout.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class Tablero(
    val idTablero: String,
    val titulo: String,
    val idOrganizacion: Long,
    val idEquipo: Long?,
    val isDeleted: Boolean = false,
    val updatedAt: Long = 0L
) // Id equipo anadido para saber pertenencia
