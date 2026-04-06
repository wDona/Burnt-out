package dev.wdona.burntout.presentation.ui.components.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.wdona.burntout.presentation.ui.components.equipo.SalirEquipoButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    title: String,
    titleIcon: @Composable (() -> Unit)? = null,
    subtitle: String? = null,
    onVolver: (() -> Unit)? = null,
    onAjustes: (() -> Unit)? = null,
    onSalirEquipo: (() -> Unit)? = null,
    onCrear: (() -> Unit)? = null,
    onSaltar: (() -> Unit)? = null,
    windowInsets: WindowInsets = WindowInsets(0, 0, 0, 0)
) {
    CenterAlignedTopAppBar(
        windowInsets = windowInsets,
        title = {
            Row (
                verticalAlignment = Alignment.Bottom,
            ){
                if (titleIcon != null) titleIcon()
                Text(
                    text = title,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                )
                if (subtitle != null) Text(
                    text = subtitle,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        },
        navigationIcon = {
            if (onVolver != null) {
                BotonVolver {
                    onVolver()
                }
            }
        },
        actions = {
            if (onSaltar != null) {
                TextButton(onClick = onSaltar) {
                    Text("Saltar")
                }
            }
            if (onAjustes != null) {
                BotonAjustes { onAjustes() }
            }
            if (onCrear != null) {
                BotonAnadir { onCrear() }
            }
            if (onSalirEquipo != null) {
                SalirEquipoButton { onSalirEquipo() }
            }
        }
    )
}