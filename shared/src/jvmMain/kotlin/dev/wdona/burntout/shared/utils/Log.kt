package dev.wdona.burntout.shared.utils

actual object Logger {
    actual fun d(tag: String, message: String) {
        println("[$tag] $message")
    }
}
