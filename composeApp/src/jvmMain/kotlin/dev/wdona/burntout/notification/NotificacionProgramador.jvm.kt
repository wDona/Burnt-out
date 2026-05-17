package dev.wdona.burntout.notification

import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.image.BufferedImage
import java.util.Date
import java.util.Timer
import java.util.TimerTask
import javax.imageio.ImageIO

actual class NotificacionProgramador {

    private val timer = Timer("notificaciones-tarea", true)
    private val tareas = mutableMapOf<String, List<TimerTask>>()

    actual fun programarNotificaciones(idTarea: String, titulo: String, fechaVencimiento: Long, notificacionPersonalizada: Long?) {
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
        if (notificacionPersonalizada != null && notificacionPersonalizada > ahora) {
            val tarea = object : TimerTask() {
                override fun run() = mostrarNotificacion("Recordatorio de tarea", "\"$titulo\" personalizado")
            }
            timer.schedule(tarea, Date(notificacionPersonalizada))
            nuevasTareas.add(tarea)
        }
        tareas[idTarea] = nuevasTareas
    }

    actual fun cancelarNotificaciones(idTarea: String) {
        tareas.remove(idTarea)?.forEach { it.cancel() }
    }

    private fun mostrarNotificacion(titulo: String, mensaje: String) {
        Thread {
            try {
                if (SystemTray.isSupported()) {
                    val tray = SystemTray.getSystemTray()
                    val img = NotificacionProgramador::class.java.getResourceAsStream("/logoBurntOutIcon.png")
                        ?.let { ImageIO.read(it) }
                        ?: BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)
                    val icon = TrayIcon(img, "BurntOut")
                    icon.isImageAutoSize = true
                    tray.add(icon)
                    icon.displayMessage(titulo, mensaje, TrayIcon.MessageType.INFO)
                    Thread.sleep(8000)
                    tray.remove(icon)
                } else {
                    println("[BurntOut] $titulo: $mensaje")
                }
            } catch (e: Exception) {
                println("[BurntOut] $titulo: $mensaje (${e.message})")
            }
        }.also { it.isDaemon = true }.start()
    }
}
