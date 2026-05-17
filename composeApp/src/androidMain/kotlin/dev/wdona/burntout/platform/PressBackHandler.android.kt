package dev.wdona.burntout.platform

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun PressBackHandler(enabled: Boolean, onBack: () -> Unit) {
    val context = LocalContext.current

    BackHandler(enabled = enabled) {
        onBack()
    }
}

