package dev.wdona.burntout

import java.util.concurrent.ConcurrentHashMap

object RateLimiter {
    private data class Window(var count: Int, val start: Long)

    private val map = ConcurrentHashMap<String, Window>()

    private const val MAX_REQUESTS = 250
    private const val WINDOW_MS = 60_000L

    fun allow(ip: String): Boolean {
        val now = System.currentTimeMillis()
        val window = map.compute(ip) { _, existing ->
            if (existing == null || now - existing.start > WINDOW_MS) {
                Window(1, now)
            } else {
                existing.also { it.count++ }
            }
        }!!
        return window.count <= MAX_REQUESTS
    }
}
