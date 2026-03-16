package dev.wdona.burntout.presentation.ui.components.tarea

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Square
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.wdona.burntout.domain.model.TipoEstadoTarea
import dev.wdona.burntout.shared.domain.Tarea

@Composable
fun CardTarea(tarea: Tarea, onClick: () -> Unit, onCompletar: () -> Unit) {

    val color = when (tarea.estado.lowercase()) {
        TipoEstadoTarea.PENDIENTE.string.lowercase() -> Color(TipoEstadoTarea.PENDIENTE.color)
        TipoEstadoTarea.EN_PROCESO.string.lowercase() -> Color(TipoEstadoTarea.EN_PROCESO.color)
        TipoEstadoTarea.COMPLETADA.string.lowercase() -> Color(TipoEstadoTarea.COMPLETADA.color)
        else -> Color.DarkGray
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Square,
            contentDescription = "Icono de tarea: ${tarea.titulo}",
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(24.dp),
            tint = color
        )
        Text(
            text = tarea.titulo,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${tarea.idUsuarioAsignado}" ,
            textAlign = TextAlign.End,
        )
    }
}