package dev.wdona.burntout.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class TareaVencimientoReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val titulo = intent.getStringExtra(EXTRA_TITULO) ?: return
        val tipo = intent.getStringExtra(EXTRA_TIPO) ?: TIPO_ENTREGA
        val idTarea = intent.getLongExtra(EXTRA_ID_TAREA, -1L)
        if (idTarea == -1L) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        crearCanalSiNecesario(notificationManager)

        val (titulo24h, cuerpo) = if (tipo == TIPO_24H) {
            "Tarea próxima a vencer" to "\"$titulo\" vence en 24 horas"
        } else {
            "Tarea vencida" to "\"$titulo\" ha llegado a su fecha de entrega"
        }

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(titulo24h)
            .setContentText(cuerpo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notifId = (idTarea * 10 + if (tipo == TIPO_24H) 0 else 1).toInt()
        notificationManager.notify(notifId, notificacion)
    }

    private fun crearCanalSiNecesario(manager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Vencimientos de tareas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Notificaciones de fechas de entrega de tareas" }
            manager.createNotificationChannel(canal)
        }
    }

    companion object {
        const val CANAL_ID = "tareas_vencimiento"
        const val EXTRA_TITULO = "titulo"
        const val EXTRA_TIPO = "tipo"
        const val EXTRA_ID_TAREA = "id_tarea"
        const val TIPO_24H = "24h"
        const val TIPO_ENTREGA = "entrega"
    }
}
