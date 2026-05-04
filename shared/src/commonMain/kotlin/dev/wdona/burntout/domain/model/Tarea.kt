package dev.wdona.burntout.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class Tarea(
    val idTarea: String,
    val titulo: String,
    val descripcion: String?,
    val estado: String,
    val idTableroPerteneciente: String,
    val idUsuarioAsignado: Long,
    val idSubtareas: List<String>?,
    val fechaVencimiento: Long? = null,
    val isDeleted: Boolean = false,
    val updatedAt: Long = 0L
)
