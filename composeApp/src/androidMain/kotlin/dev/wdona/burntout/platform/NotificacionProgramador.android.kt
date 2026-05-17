package dev.wdona.burntout.platform

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dev.wdona.burntout.notification.TareaVencimientoReceiver
import dev.wdona.burntout.shared.utils.SettingsManager

actual class NotificacionProgramador(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    actual fun programarNotificaciones(idTarea: String, titulo: String, fechaVencimiento: Long, notificacionPersonalizada: Long?) {
        if (!SettingsManager.isNotificacionesActivas()) return
        val ahora = System.currentTimeMillis()
        val ms24h = 24 * 60 * 60 * 1000L

        if (fechaVencimiento - ms24h > ahora) {
            programarAlarma(idTarea, titulo, fechaVencimiento - ms24h, TareaVencimientoReceiver.TIPO_24H)
        }
        if (fechaVencimiento > ahora) {
            programarAlarma(idTarea, titulo, fechaVencimiento, TareaVencimientoReceiver.TIPO_ENTREGA)
        }
        if (notificacionPersonalizada != null && notificacionPersonalizada > ahora) {
            programarAlarma(idTarea, titulo, notificacionPersonalizada, TareaVencimientoReceiver.TIPO_CUSTOM)
        }
    }

    actual fun cancelarNotificaciones(idTarea: String) {
        cancelarAlarma(idTarea, TareaVencimientoReceiver.TIPO_24H)
        cancelarAlarma(idTarea, TareaVencimientoReceiver.TIPO_ENTREGA)
        cancelarAlarma(idTarea, TareaVencimientoReceiver.TIPO_CUSTOM)
    }

    private fun tipoOffset(tipo: String) = when (tipo) {
        TareaVencimientoReceiver.TIPO_24H -> 0
        TareaVencimientoReceiver.TIPO_CUSTOM -> 2
        else -> 1
    }

    private fun programarAlarma(idTarea: String, titulo: String, triggerMs: Long, tipo: String) {
        val intent = Intent(context, TareaVencimientoReceiver::class.java).apply {
            putExtra(TareaVencimientoReceiver.EXTRA_TITULO, titulo)
            putExtra(TareaVencimientoReceiver.EXTRA_TIPO, tipo)
            putExtra(TareaVencimientoReceiver.EXTRA_ID_TAREA, idTarea)
        }
        val requestCode = idTarea.hashCode() * 10 + tipoOffset(tipo)
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        try {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms() ->
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pendingIntent)
                true ->
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pendingIntent)
                else ->
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMs, pendingIntent)
            }
        } catch (e: SecurityException) {
            println("Sin permiso para programar alarmas exactas: ${e.message}")
        }
    }

    private fun cancelarAlarma(idTarea: String, tipo: String) {
        val intent = Intent(context, TareaVencimientoReceiver::class.java)
        val requestCode = idTarea.hashCode() * 10 + tipoOffset(tipo)
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let { alarmManager.cancel(it) }
    }
}
