package dev.wdona.burntout.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Ajuste(
    val idAjuste: Long? = null,
    val idUsuario: Long,
    val nombre: String,
    val valorAjuste: String,
    val isDeleted: Boolean = false,
    val updatedAt: Long = 0L
)
