package dev.wdona.burntout.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.wdona.burntout.shared.db.DatabaseActions
import dev.wdona.burntout.shared.db.DatabaseDriverFactory
import dev.wdona.burntout.shared.utils.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != "android.intent.action.QUICKBOOT_POWERON"
        ) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                DatabaseActions.driverFactory = { DatabaseDriverFactory(context.applicationContext).createDriver() }
                val database = DatabaseActions.getDatabase()
                val idUsuario = SettingsManager.getIdUsuarioActual()
                if (SettingsManager.isUsuarioInvitado()) return@launch
                if (!SettingsManager.isNotificacionesActivas()) return@launch

                val ahora = System.currentTimeMillis() / 1000
                val notificacionProgramador = NotificacionProgramador(context.applicationContext)

                database.appDatabaseQueries.getTareasConFechaByUsuario(idUsuario)
                    .executeAsList()
                    .filter { (it.Fecha_Vencimiento ?: 0L) > ahora }
                    .forEach { tarea ->
                        val fecha = tarea.Fecha_Vencimiento ?: return@forEach
                        notificacionProgramador.programarNotificaciones(
                            idTarea = tarea.ID_Tarea,
                            titulo = tarea.Titulo,
                            fechaVencimiento = fecha * 1000L,
                            notificacionPersonalizada = tarea.Notificacion_Personalizada?.let { it * 1000L }
                        )
                    }
            } catch (e: Exception) {
                println("[BurntOut] Error reprogramando notificaciones tras reboot: ${e.message}")
            } finally {
                pendingResult.finish()
            }
        }
    }
}
