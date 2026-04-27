package dev.wdona.burntout.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class Subtarea(
    val idSubtarea: Long,
    val titulo: String,
    val descripcion: String?,
    val completado: Boolean,
    val idTareaPerteneciente: Long,
    val isDeleted: Boolean = false
)
