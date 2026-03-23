package dev.wdona.burntout.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Respuesta(
    val idUsuario: Long,
    val idPregunta: Long,
    val anonimo: Boolean,
    val respuesta: Long,
    val nombreUsuario: String? = null,
    val fecha: Long? = null
)
