package dev.wdona.burntout.presentation.ui.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun BotonAjustes(onAjustes: () -> Unit) {
    IconButton(
        onClick = onAjustes
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Ajustes"
            )
    }
}