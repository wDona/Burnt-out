package dev.wdona.burntout.shared.utils
import java.time.LocalDate

actual fun getCurrentDateString(): String {
    return LocalDate.now().toString()
}

actual fun getCurrentTimestampSeconds(): Long {
    return System.currentTimeMillis() / 1000
}

