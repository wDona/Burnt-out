package dev.wdona.burntout.platform

import dev.wdona.burntout.BuildConfig

actual object AppInfo {
    actual val version: String = BuildConfig.VERSION_NAME
}

