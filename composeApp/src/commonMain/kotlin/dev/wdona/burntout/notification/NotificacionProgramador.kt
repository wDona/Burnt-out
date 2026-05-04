package dev.wdona.burntout.notification

expect class NotificacionProgramador {
    fun programarNotificaciones(idTarea: String, titulo: String, fechaVencimiento: Long)
    fun cancelarNotificaciones(idTarea: String)
}
