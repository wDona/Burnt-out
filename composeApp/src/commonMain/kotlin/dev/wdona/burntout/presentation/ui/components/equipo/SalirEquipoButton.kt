package dev.wdona.burntout.presentation.ui.components.equipo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun SalirEquipoButton(onSalirEquipo: () -> Unit) {
    IconButton(
        onClick = { onSalirEquipo() }
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = "Salir del equipo"
        )
    }
}