package dev.wdona.burntout.platform

import kotlinx.coroutines.flow.Flow

expect class NetworkObserver(context: Any?) {
    val isConnected: Flow<Boolean>
}
