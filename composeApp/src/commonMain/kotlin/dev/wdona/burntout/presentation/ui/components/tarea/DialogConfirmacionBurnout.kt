package dev.wdona.burntout.presentation.ui.components.tarea

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DialogConfirmacionBurnout(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmación requerida") },
        text = { Text("Soy consciente de que le asigno una tarea a un trabajador con burnout") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Acepto") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}