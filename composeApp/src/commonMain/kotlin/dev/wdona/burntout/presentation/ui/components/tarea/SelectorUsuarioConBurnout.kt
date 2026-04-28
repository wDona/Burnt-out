package dev.wdona.burntout.presentation.ui.components.tarea

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import dev.wdona.burntout.presentation.ui.components.common.BateriaBurnout
import dev.wdona.burntout.presentation.ui.components.common.getEstadoBateria
import dev.wdona.burntout.shared.domain.Usuario

private val UMBRAL_ADVERTENCIA = 0.40
private val UMBRAL_DIALOG = 0.80

fun nivelRequiereDialog(riesgo: Double?) = (riesgo ?: -1.0) > UMBRAL_DIALOG
fun nivelRequiereAdvertencia(riesgo: Double?) = (riesgo ?: -1.0) > UMBRAL_ADVERTENCIA

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorUsuarioConBurnout(
    miembros: List<Usuario>,
    selectedUsuario: Usuario?,
    onUsuarioSelected: (Usuario) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val labelText = selectedUsuario?.nombre ?: "Sin asignar"
    val riesgo = selectedUsuario?.riesgoBurnout ?: -1.0

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = labelText,
                onValueChange = {},
                readOnly = true,
                label = { Text("Asignado a") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                leadingIcon = if (selectedUsuario != null) {
                    { BateriaBurnout(riesgo, mostrarTexto = false, size = 20) }
                } else null
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                miembros.forEach { usuario ->
                    val r = usuario.riesgoBurnout ?: -1.0
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                BateriaBurnout(r, mostrarTexto = false, size = 20)
                                Text(
                                    text = usuario.nombre,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        },
                        onClick = {
                            onUsuarioSelected(usuario)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (selectedUsuario != null && nivelRequiereAdvertencia(selectedUsuario.riesgoBurnout)) {
            val (_, color, texto) = getEstadoBateria(riesgo)
            Text(
                text = "⚠ Cuidado, este usuario tiene $texto",
                color = color,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

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
