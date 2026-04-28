package dev.wdona.burntout.presentation.ui.components.equipo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
                    singleLine = true
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
                onClick = { onAddUsuario(input) },
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