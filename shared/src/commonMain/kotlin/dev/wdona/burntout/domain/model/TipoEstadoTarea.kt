package dev.wdona.burntout.domain.model


enum class TipoEstadoTarea(val string: String, val color: Long) {
    PENDIENTE("Pendiente", 0xFFFFFFFF), // Blanco
    EN_PROCESO("En Proceso", 0xFFFFFF00), // Amarillo
    COMPLETADA("Completada", 0xFF00FF00) // Verde
}

