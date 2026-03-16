package dev.wdona.burntout.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class Respuesta(
    val idUsuario: Long,
    val idPregunta: Long,
    val anonimo: Boolean,
    val respuesta: String,
    val nombreUsuario: String? = null
)
