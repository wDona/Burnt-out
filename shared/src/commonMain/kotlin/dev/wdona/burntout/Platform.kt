package dev.wdona.burntout

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform