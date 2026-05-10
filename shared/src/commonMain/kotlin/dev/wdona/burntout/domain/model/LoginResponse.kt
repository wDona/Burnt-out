package dev.wdona.burntout.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val usuario: Usuario,
    val token: String
)
