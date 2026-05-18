package dev.wdona.burntout.presentation.ui.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun DialogoConfirmacionEliminar(
    username: String,
    tituloInicial: String = "¿Eliminar usuario?",
    onConfirmar: () -> Unit,
    onDismiss: () -> Unit
) {
    var mostrarPaso2 by remember { mutableStateOf(false) }
    var textoConfirmacion by remember { mutableStateOf("") }
    val textoEsperado = "eliminar $username"

    if (!mostrarPaso2) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(tituloInicial) },
            text = { Text("¿Seguro que lo quieres eliminar? Esta acción es irrecuperable. Toda su información se perderá.") },
            confirmButton = {
                TextButton(onClick = { mostrarPaso2 = true }) {
                    Text("Continuar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Confirmar eliminación") },
            text = {
                Column {
                    Text("Escribe \"$textoEsperado\" para confirmar:")
                    OutlinedTextField(
                        value = textoConfirmacion,
                        onValueChange = { textoConfirmacion = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            if (textoConfirmacion == textoEsperado) {
                                onConfirmar()
                            }
                        })
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = textoConfirmacion == textoEsperado,
                    onClick = onConfirmar
                ) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        )
    }
}

