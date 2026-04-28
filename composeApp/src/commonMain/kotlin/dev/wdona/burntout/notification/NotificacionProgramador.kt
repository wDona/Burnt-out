package dev.wdona.burntout.notification

expect class NotificacionProgramador {
    fun programarNotificaciones(idTarea: Long, titulo: String, fechaVencimiento: Long)
    fun cancelarNotificaciones(idTarea: Long)
}
