package dev.wdona.burntout.notification

import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.image.BufferedImage
import java.util.Date
import java.util.Timer
import java.util.TimerTask

actual class NotificacionProgramador {

    private val timer = Timer("notificaciones-tarea", true)
    private val tareas = mutableMapOf<Long, List<TimerTask>>()

    actual fun programarNotificaciones(idTarea: Long, titulo: String, fechaVencimiento: Long) {
        cancelarNotificaciones(idTarea)
        val ahora = System.currentTimeMillis()
        val ms24h = 24 * 60 * 60 * 1000L
        val nuevasTareas = mutableListOf<TimerTask>()

        if (fechaVencimiento - ms24h > ahora) {
            val tarea = object : TimerTask() {
                override fun run() = mostrarNotificacion("Tarea próxima a vencer", "\"$titulo\" vence en 24 horas")
            }
            timer.schedule(tarea, Date(fechaVencimiento - ms24h))
            nuevasTareas.add(tarea)
        }
        if (fechaVencimiento > ahora) {
            val tarea = object : TimerTask() {
                override fun run() = mostrarNotificacion("Tarea vencida", "\"$titulo\" ha llegado a su fecha de entrega")
            }
            timer.schedule(tarea, Date(fechaVencimiento))
            nuevasTareas.add(tarea)
        }
        tareas[idTarea] = nuevasTareas
    }

    actual fun cancelarNotificaciones(idTarea: Long) {
        tareas.remove(idTarea)?.forEach { it.cancel() }
    }

    private fun mostrarNotificacion(titulo: String, mensaje: String) {
        try {
            if (SystemTray.isSupported()) {
                val tray = SystemTray.getSystemTray()
                val img = BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)
                val icon = TrayIcon(img, "BurntOut")
                icon.isImageAutoSize = true
                tray.add(icon)
                icon.displayMessage(titulo, mensaje, TrayIcon.MessageType.INFO)
                Thread.sleep(5000)
                tray.remove(icon)
            }
        } catch (e: Exception) {
            println("Notificacion: $titulo — $mensaje")
        }
    }
}
