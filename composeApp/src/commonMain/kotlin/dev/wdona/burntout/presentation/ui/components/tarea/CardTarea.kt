package dev.wdona.burntout.presentation.ui.components.tarea

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Square
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.wdona.burntout.domain.model.TipoEstadoTarea
import dev.wdona.burntout.presentation.ui.components.common.formatearFecha
import dev.wdona.burntout.shared.domain.Tarea

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardTarea(tarea: Tarea, nombreAsignado: String, onClick: () -> Unit, onDelete: () -> Unit, onCompletar: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    val color = when (tarea.estado.lowercase()) {
        TipoEstadoTarea.PENDIENTE.string.lowercase() -> Color(TipoEstadoTarea.PENDIENTE.color)
        TipoEstadoTarea.EN_PROCESO.string.lowercase() -> Color(TipoEstadoTarea.EN_PROCESO.color)
        TipoEstadoTarea.COMPLETADA.string.lowercase() -> Color(TipoEstadoTarea.COMPLETADA.color)
        else -> Color.DarkGray
    }

    val icon = when (tarea.estado.lowercase()) {
        TipoEstadoTarea.PENDIENTE.string.lowercase() -> Icons.Default.RadioButtonUnchecked
        TipoEstadoTarea.EN_PROCESO.string.lowercase() -> Icons.Default.MoreHoriz
        TipoEstadoTarea.COMPLETADA.string.lowercase() -> Icons.Default.CheckCircle
        else -> Icons.Default.Square
    }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = { showMenu = true }
                )
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (tarea.estado.lowercase() != TipoEstadoTarea.COMPLETADA.string.lowercase()) onCompletar() },
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Estado de tarea: ${tarea.estado}",
                    modifier = Modifier.padding(top = 2.dp),
                    tint = color
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarea.titulo,
                    textAlign = TextAlign.Start
                )
                tarea.fechaVencimiento?.let { fecha ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Fecha de vencimiento",
                            modifier = Modifier.padding(end = 4.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = formatearFecha(fecha),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            Text(
                text = nombreAsignado,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(end = 8.dp)
            )
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Opciones de tarea")
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            if (tarea.estado.lowercase() != TipoEstadoTarea.COMPLETADA.string.lowercase()) {
                DropdownMenuItem(
                    text = { Text("Completar") },
                    onClick = {
                        onCompletar()
                        showMenu = false
                    },
                    leadingIcon = {
                        Icon(Icons.Default.CheckCircleOutline, contentDescription = null)
                    }
                )
            }
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
