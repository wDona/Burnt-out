package dev.wdona.burntout.presentation.ui.components.equipo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.Person3
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        val tint: androidx.compose.ui.graphics.Color
        miembro.riesgoBurnout.let {
            if (it != null) {
                tint = if (it < 0.33) {
                    BurntOutMaterialTheme.getSuccessColor()
                } else if (it in 0.33..0.66) {
                    BurntOutMaterialTheme.getWarningColor()
                } else {
                    BurntOutMaterialTheme.getErrorColor()
                }
            } else {
                tint = BurntOutMaterialTheme.getColorScheme().onSurface
            }
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Icono de usuario",
                modifier = Modifier.padding(start = 16.dp, end = 8.dp),
                tint = tint
            )
            Text(
                text = "$id ${miembro.nombre}",
                style = MaterialTheme.typography.titleMedium,
                color = tint
            )
        }
    }
}
