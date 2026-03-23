package dev.wdona.burntout.presentation.ui.components.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FAB(
    textoBoton: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    iconFAB: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val icon = iconFAB ?: { Icon(Icons.Default.Add, contentDescription = "Crear") }

    if (enabled) {
        ExtendedFloatingActionButton(
            onClick = onClick,
            icon = { icon() },
            text = { Text(textoBoton) },
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
        )
    } else {
        ExtendedFloatingActionButton(
            onClick = {},
            icon = { icon() },
            text = { Text(textoBoton) },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier
        )
    }
}