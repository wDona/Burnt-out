package dev.wdona.burntout

actual object AppInfo {
    actual val version: String = System.getProperty("app.version") ?: "1.1.14"
}

