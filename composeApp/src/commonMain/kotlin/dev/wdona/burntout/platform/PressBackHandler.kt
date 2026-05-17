package dev.wdona.burntout.platform

import androidx.compose.runtime.Composable

@Composable
expect fun PressBackHandler(enabled: Boolean, onBack: () -> Unit)