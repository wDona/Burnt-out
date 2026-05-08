package dev.wdona.burntout.presentation.ui.components.equipo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun RenombrarEquipoDialog(nombreActual: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var nombre by remember { mutableStateOf(nombreActual) }

    fun confirmar() { if (nombre.isNotBlank()) onConfirm(nombre) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Renombrar equipo") },
        text = {
            Column {
                Text("Introduce el nuevo nombre:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del equipo") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { confirmar() }),
                    modifier = Modifier.onKeyEvent { keyEvent ->
                        if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyDown) {
                            confirmar(); true
                        } else false
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = ::confirmar, enabled = nombre.isNotBlank()) {
                Text("Renombrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun CrearEquipoDialog(defaultNombre: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var nombre by remember { mutableStateOf(defaultNombre) }

    fun confirmar() { if (nombre.isNotBlank()) onConfirm(nombre) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear nuevo equipo") },
        text = {
            Column {
                Text("Introduce el nombre para tu nuevo equipo:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del equipo") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { confirmar() }),
                    modifier = Modifier.onKeyEvent { keyEvent ->
                        if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyDown) {
                            confirmar(); true
                        } else false
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = ::confirmar, enabled = nombre.isNotBlank()) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
