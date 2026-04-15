package dev.wdona.burntout.shared.utils

import android.util.Log

actual object Logger {
    actual fun d(tag: String, message: String) {
        Log.d(tag, message)
    }
}
