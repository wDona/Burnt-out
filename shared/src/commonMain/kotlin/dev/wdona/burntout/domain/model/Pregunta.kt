package dev.wdona.burntout.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class Pregunta(
    val idPregunta: Long,
    val pregunta: String,
    val idOrganizacion: Long,
    val categoria: String = "CE" // Default para compatibilidad
)
