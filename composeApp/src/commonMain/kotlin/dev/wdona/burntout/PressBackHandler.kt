package dev.wdona.burntout

import androidx.compose.runtime.Composable

@Composable
expect fun PressBackHandler(enabled: Boolean, onBack: () -> Unit)