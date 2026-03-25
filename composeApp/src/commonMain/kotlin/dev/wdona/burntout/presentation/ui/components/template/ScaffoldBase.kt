package dev.wdona.burntout.presentation.ui.components.template

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.wdona.burntout.presentation.ui.components.common.FAB
import dev.wdona.burntout.presentation.ui.components.common.MainTopBar

/**
 *
 */
@Composable
fun ScaffoldBase(
    titulo: String = "",
    titleIcon: @Composable (() -> Unit)? = null,
    subtitle: String? = null,
    onVolver: (() -> Unit)? = null,
    onAjustes: (() -> Unit)? = null,
    onFAB: (() -> Unit)? = null,
    onSaltar: (() -> Unit)? = null,
    fabEnabled: Boolean = true,
    textoFAB: String? = null,
    iconFAB: @Composable (() -> Unit)? = null,
    bottomBar: @Composable () -> Unit = {},
    topBarWindowInsets: WindowInsets = WindowInsets(0, 0, 0, 0),
    fabModifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            MainTopBar(
                title = titulo,
                subtitle = subtitle,
                onVolver = onVolver,
                onAjustes = onAjustes,
                titleIcon = titleIcon,
                onSaltar = onSaltar,
                windowInsets = topBarWindowInsets
            )
        },
        floatingActionButton = {
            if (onFAB != null) {
                FAB(
                    textoBoton = textoFAB ?: "Nuevo",
                    onClick = onFAB,
                    enabled = fabEnabled,
                    iconFAB = iconFAB,
                    modifier = fabModifier
                )
            }
        },
        bottomBar = bottomBar
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            content(PaddingValues(0.dp))
        }
    }
}
