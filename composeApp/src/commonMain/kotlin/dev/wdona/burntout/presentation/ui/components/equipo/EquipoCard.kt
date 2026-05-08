package dev.wdona.burntout.presentation.ui.components.equipo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups2
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.unit.dp
import dev.wdona.burntout.shared.domain.Equipo

@Composable
fun EquipoCard(equipo: Equipo, onClick: () -> Unit, onRenombrar: (() -> Unit)? = null) {
    var mostrarMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(bottom = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Groups2,
            contentDescription = "Icono de usuario",
            modifier = Modifier.padding(start = 16.dp, end = 8.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = equipo.titulo,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Nvl: ${equipo.nivel} | ${equipo.puntosRestantes}/${equipo.costoSiguienteNivel} exp",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (onRenombrar != null) {
            Box {
                IconButton(onClick = { mostrarMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                }
                DropdownMenu(
                    expanded = mostrarMenu,
                    onDismissRequest = { mostrarMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Renombrar") },
                        onClick = {
                            mostrarMenu = false
                            onRenombrar()
                        }
                    )
                }
            }
        }
    }
}
