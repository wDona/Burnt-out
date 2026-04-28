package dev.wdona.burntout.presentation.ui.components.tablero

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.wdona.burntout.shared.domain.Tablero

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardTablero(tablero: Tablero, onClick: () -> Unit, onDelete: () -> Unit, onRename: (String) -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var nuevoNombre by remember { mutableStateOf(tablero.titulo) }

    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Renombrar tablero") },
            text = {
                OutlinedTextField(
                    value = nuevoNombre,
                    onValueChange = { nuevoNombre = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (nuevoNombre.isNotBlank()) {
                            onRename(nuevoNombre.trim())
                        }
                        showRenameDialog = false
                    }
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = { showMenu = true }
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = "Tablero: ${tablero.titulo}",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = tablero.titulo,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Renombrar") },
                onClick = {
                    nuevoNombre = tablero.titulo
                    showMenu = false
                    showRenameDialog = true
                },
                leadingIcon = {
                    Icon(Icons.Default.DriveFileRenameOutline, contentDescription = null)
                }
            )
            DropdownMenuItem(
                text = { Text("Eliminar") },
                onClick = {
                    onDelete()
                    showMenu = false
                },
                leadingIcon = {
                    Icon(Icons.Default.Delete, contentDescription = null)
                }
            )
        }
    }
}
