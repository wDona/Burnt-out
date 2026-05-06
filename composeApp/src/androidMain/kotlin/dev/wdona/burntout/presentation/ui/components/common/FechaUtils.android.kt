package dev.wdona.burntout.presentation.ui.components.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun formatearFecha(epochMs: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(epochMs))
}
