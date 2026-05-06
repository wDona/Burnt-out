package dev.wdona.burntout.presentation.ui.components.equipo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Groups2
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.wdona.burntout.shared.domain.Equipo

@Composable
fun EquipoCard(equipo: Equipo, onClick: () -> Unit) {
    Row (
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
        Column(

        ){
            Text(
                text = equipo.titulo,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Nvl: ${equipo.nivel} | ${equipo.puntosRestantes}/${equipo.costoSiguienteNivel} exp",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}