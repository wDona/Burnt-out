package dev.wdona.burntout.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class Equipo(
    val idEquipo: Long,
    val titulo: String,
    val puntuacion: Long?,
    val idOrganizacion: Long,
    val idMiembros: List<Long>,
    val isDeleted: Boolean = false,
    val updatedAt: Long = 0L
) // Puntuacion anadida
