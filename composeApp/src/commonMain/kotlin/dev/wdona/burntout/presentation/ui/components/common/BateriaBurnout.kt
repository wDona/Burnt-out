package dev.wdona.burntout.presentation.ui.components.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Battery0Bar
import androidx.compose.material.icons.filled.Battery1Bar
import androidx.compose.material.icons.filled.Battery2Bar
import androidx.compose.material.icons.filled.Battery3Bar
import androidx.compose.material.icons.filled.Battery4Bar
import androidx.compose.material.icons.filled.Battery5Bar
import androidx.compose.material.icons.filled.Battery6Bar
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.wdona.burntout.presentation.ui.theme.BurntOutMaterialTheme
import kotlin.math.round

@Composable
fun BateriaBurnout(riesgo: Double, mostrarTexto: Boolean = true, size: Int = 24, modifier: Modifier = Modifier) {
    val (icon, color, text) = getEstadoBateria(riesgo)
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = color,
            modifier = modifier.size(size.dp)
        )
        if (mostrarTexto) {
            val riesgoVisual = round(riesgo * 100) / 100.0
            Text(
                text = "$text ($riesgoVisual)",
                style = MaterialTheme.typography.bodyMedium,
                color = color,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun getEstadoBateria(riesgo: Double): Triple<ImageVector, Color, String> {
    return when {
        riesgo < 0 -> Triple (
            Icons.Default.BatteryFull,
            BurntOutMaterialTheme.getColorScheme().onSurface,
            "Sin responder"
        )
        riesgo < 0.10 -> Triple(
            Icons.Default.BatteryFull,
            BurntOutMaterialTheme.getSuccessColor(),
            "Sin riesgo"
        )
        riesgo < 0.20 -> Triple(
            Icons.Default.Battery6Bar,
            Color(0xFFCCFF00),
            "Riesgo bajo"
        )
        riesgo < 0.35 -> Triple(
            Icons.Default.Battery5Bar,
            Color(0xFFCCFF00),
            "Riesgo bajo"
        )
        riesgo < 0.50 -> Triple(
            Icons.Default.Battery4Bar,
            Color(0xFFFFCC00),
            "Riesgo medio"
        )
        riesgo < 0.65 -> Triple(
            Icons.Default.Battery3Bar,
            Color(0xFFFFCC00),
            "Riesgo medio"
        )
        riesgo < 0.80 -> Triple(
            Icons.Default.Battery2Bar,
            Color(0xFFFF6600),
            "Riesgo alto"
        )
        riesgo < 0.90 -> Triple(
            Icons.Default.Battery1Bar,
            Color(0xFFFF6600),
            "Riesgo alto"
        )
        else -> Triple(
            Icons.Default.Battery0Bar,
            BurntOutMaterialTheme.getErrorColor(),
            "Burnout"
        )
    }
}
