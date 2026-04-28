package dev.wdona.burntout.presentation.ui.components.equipo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import dev.wdona.burntout.presentation.ui.components.common.BateriaBurnout
import dev.wdona.burntout.presentation.ui.theme.BurntOutMaterialTheme
import dev.wdona.burntout.shared.domain.Usuario

@Composable
fun MiembroCard(miembro: Usuario, onClick: () -> Unit) {
    val id = if (miembro.idUsuario == Long.MIN_VALUE) {
        ""
    } else {
        miembro.idUsuario.toString()
    }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(bottom = 8.dp)
            .clickable { onClick() },

        verticalAlignment = Alignment.CenterVertically
    ){
        val riesgo = miembro.riesgoBurnout
        if (riesgo != null) {
            BateriaBurnout(riesgo = riesgo, mostrarTexto = false, modifier = Modifier.padding(start = 16.dp, end = 8.dp))
        } else {
            BateriaBurnout(riesgo = -1.0, mostrarTexto = false, modifier = Modifier.padding(start = 16.dp, end = 8.dp))
        }
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Icono de usuario",
            modifier = Modifier.padding(end = 8.dp),
            tint = BurntOutMaterialTheme.getColorScheme().onSurface
        )
        Text(
            text = miembro.nombre,
            style = MaterialTheme.typography.titleMedium,
            color = BurntOutMaterialTheme.getColorScheme().onSurface,
        )

        Text(
            text = miembro.username,
            style = MaterialTheme.typography.bodyMedium,
            color = BurntOutMaterialTheme.getColorScheme().onSurface,
            modifier = Modifier.padding(start = 8.dp).alpha(0.7f)
        )

    }
}
