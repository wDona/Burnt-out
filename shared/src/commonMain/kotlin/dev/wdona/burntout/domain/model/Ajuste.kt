package dev.wdona.burntout.domain.model

import kotlinx.serialization.Serializable

@Serializable
class Ajuste(
    val idAjuste: Long,
    val nombre: String,
    val valorAjuste: String,
    val isDeleted: Boolean = false
) {
}