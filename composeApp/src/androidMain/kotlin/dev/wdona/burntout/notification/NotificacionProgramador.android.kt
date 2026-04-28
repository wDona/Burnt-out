package dev.wdona.burntout.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

actual class NotificacionProgramador(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    actual fun programarNotificaciones(idTarea: Long, titulo: String, fechaVencimiento: Long) {
        val ahora = System.currentTimeMillis()
        val ms24h = 24 * 60 * 60 * 1000L

        if (fechaVencimiento - ms24h > ahora) {
            programarAlarma(idTarea, titulo, fechaVencimiento - ms24h, TareaVencimientoReceiver.TIPO_24H)
        }
        if (fechaVencimiento > ahora) {
            programarAlarma(idTarea, titulo, fechaVencimiento, TareaVencimientoReceiver.TIPO_ENTREGA)
        }
    }

    actual fun cancelarNotificaciones(idTarea: Long) {
        cancelarAlarma(idTarea, TareaVencimientoReceiver.TIPO_24H)
        cancelarAlarma(idTarea, TareaVencimientoReceiver.TIPO_ENTREGA)
    }

    private fun programarAlarma(idTarea: Long, titulo: String, triggerMs: Long, tipo: String) {
        val intent = Intent(context, TareaVencimientoReceiver::class.java).apply {
            putExtra(TareaVencimientoReceiver.EXTRA_TITULO, titulo)
            putExtra(TareaVencimientoReceiver.EXTRA_TIPO, tipo)
            putExtra(TareaVencimientoReceiver.EXTRA_ID_TAREA, idTarea)
        }
        val requestCode = (idTarea * 10 + if (tipo == TareaVencimientoReceiver.TIPO_24H) 0 else 1).toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMs, pendingIntent)
            }
        } catch (e: SecurityException) {
            println("Sin permiso para programar alarmas exactas: ${e.message}")
        }
    }

    private fun cancelarAlarma(idTarea: Long, tipo: String) {
        val intent = Intent(context, TareaVencimientoReceiver::class.java)
        val requestCode = (idTarea * 10 + if (tipo == TareaVencimientoReceiver.TIPO_24H) 0 else 1).toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let { alarmManager.cancel(it) }
    }
}
