package dev.wdona.burntout.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class Tarea(
    val idTarea: Long,
    val titulo: String,
    val descripcion: String?,
    val estado: String,
    val idTableroPerteneciente: Long,
    val idUsuarioAsignado: Long,
    val idSubtareas: List<Long>?,
    val isDeleted: Boolean = false
)
