package dev.wdona.burntout.presentation.ui.components.equipo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import dev.wdona.burntout.presentation.viewmodel.viewmodels.LeaderboardUiState

@Composable
fun InvitarOrgDialog(
    uiState: LeaderboardUiState,
    onGenerar: (rol: String) -> Unit,
    onDismiss: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    var rolSeleccionado by remember { mutableStateOf("MEMBER") }

    val esperandoAccion = !uiState.isGenerandoInvitacion
        && uiState.invitacionCode == null
        && uiState.invitacionError == null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Código de invitación") },
        text = {
            Column {
                when {
                    uiState.isGenerandoInvitacion -> {
                        CircularProgressIndicator()
                    }
                    uiState.invitacionError != null -> {
                        Text(
                            text = uiState.invitacionError,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    uiState.invitacionCode != null -> {
                        Text("Comparte este código para unirse a la organización:")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.invitacionCode,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = {
                            clipboard.setText(AnnotatedString(uiState.invitacionCode))
                        }) {
                            Text("Copiar código")
                        }
                    }
                    else -> {
                        Text("Selecciona el rol del invitado:")
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = rolSeleccionado == "MEMBER",
                                onClick = { rolSeleccionado = "MEMBER" },
                                label = { Text("Miembro") }
                            )
                            FilterChip(
                                selected = rolSeleccionado == "ADMIN",
                                onClick = { rolSeleccionado = "ADMIN" },
                                label = { Text("Admin") }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (esperandoAccion) {
                TextButton(onClick = { onGenerar(rolSeleccionado) }) { Text("Generar") }
            }
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}
