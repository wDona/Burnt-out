package dev.wdona.burntout.presentation.ui.components.equipo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dev.wdona.burntout.presentation.viewmodel.viewmodels.EquipoUiState

@Composable
fun AnadirUsuarioDialog(
    onDismiss: () -> Unit,
    onAddUsuario: (String) -> Unit,
    onClearError: () -> Unit,
    uiState: EquipoUiState
) {
    var input by remember { mutableStateOf("") }

    fun confirmar() { if (input.isNotBlank() && !uiState.isLoading) onAddUsuario(input) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir usuario al equipo") },
        text = {
            Column {
                Text("Introduce el nombre de usuario de la persona que quieres añadir:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = input,
                    onValueChange = {
                        input = it
                        if (uiState.error != null) onClearError()
                    },
                    label = { Text("Nombre de usuario") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { confirmar() }),
                    modifier = Modifier.onKeyEvent { keyEvent ->
                        if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyDown) {
                            confirmar(); true
                        } else false
                    }
                )
                if (input.isBlank()) {
                    Text(
                        text = "El nombre de usuario no puede estar vacío",
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (uiState.error != null) {
                    Text(
                        text = uiState.error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = ::confirmar,
                enabled = input.isNotBlank() && !uiState.isLoading
            ) {
                Text("Añadir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
