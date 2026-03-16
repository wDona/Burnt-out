package dev.wdona.burntout.presentation.ui.components.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun BotonAnadir(accion: () -> Unit) {
    TextButton(onClick = { accion() }) {
        Text(text = "+",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
