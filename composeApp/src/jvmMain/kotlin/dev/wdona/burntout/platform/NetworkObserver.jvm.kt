package dev.wdona.burntout.platform

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

actual class NetworkObserver actual constructor(context: Any?) {
    actual val isConnected: Flow<Boolean> = flow {
        while (true) {
            val connected = try {
                val socket = java.net.Socket()
                socket.connect(java.net.InetSocketAddress("8.8.8.8", 53), 1500)
                socket.close()
                true
            } catch (_: Exception) { false }
            emit(connected)
            delay(2000)
        }
    }.distinctUntilChanged()
}
