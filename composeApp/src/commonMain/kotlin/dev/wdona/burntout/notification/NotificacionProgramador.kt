package dev.wdona.burntout.notification

expect class NotificacionProgramador {
    fun programarNotificaciones(idTarea: String, titulo: String, fechaVencimiento: Long, notificacionPersonalizada: Long? = null)
    fun cancelarNotificaciones(idTarea: String)
}
