package dev.wdona.burntout.presentation.ui.components.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    title: String,
    titleIcon: @Composable (() -> Unit)? = null,
    onVolver: (() -> Unit)? = null,
    onAjustes: (() -> Unit)? = null,
    onCrear: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){
                if (titleIcon != null) titleIcon()
                Text(
                    text = title,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
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
            if (onAjustes != null) {
                BotonAjustes { onAjustes() }
            }
            if (onCrear != null) {
                BotonAnadir { onCrear() }
            }
        },
        windowInsets = WindowInsets(0, 0, 0, 0)
    )
}