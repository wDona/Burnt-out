package dev.wdona.burntout.daemon

import kotlinx.serialization.Serializable

@Serializable
data class DaemonConfig(
    val idUsuario: Long,
    val dbPath: String,
    val notificacionesActivas: Boolean,
    val host: String
)
