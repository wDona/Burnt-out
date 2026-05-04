package dev.wdona.burntout.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class Subtarea(
    val idSubtarea: String,
    val titulo: String,
    val descripcion: String?,
    val completado: Boolean,
    val idTareaPerteneciente: String,
    val isDeleted: Boolean = false,
    val updatedAt: Long = 0L
)
