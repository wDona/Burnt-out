package dev.wdona.burntout.presentation.ui.components.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun FABCrear(textoBoton: String, onIrACrear: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = onIrACrear,
        icon = { Icon(Icons.Default.Add, contentDescription = "Crear") },
        text = { Text(textoBoton) },
        shape = RoundedCornerShape(16.dp)
    )
}