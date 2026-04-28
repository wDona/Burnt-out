package dev.wdona.burntout.presentation.ui.components.equipo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import dev.wdona.burntout.presentation.viewmodel.viewmodels.LeaderboardUiState

@Composable
fun InvitarOrgDialog(uiState: LeaderboardUiState, onDismiss: () -> Unit) {
    val clipboardManager = LocalClipboardManager.current
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
                            clipboardManager.setText(AnnotatedString(uiState.invitacionCode))
                        }) {
                            Text("Copiar código")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}
