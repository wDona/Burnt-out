package dev.wdona.burntout.domain.model


data class OperacionPendiente(
    val idAccion: Long,
    val tipoAccion: String,
    val tablaAfectada: String,
    val idAfectado: String,
    val datosJson: String,
    val timestampCreacion: Long,
    val sincronizado: Boolean
)