package dev.wdona.burntout.data.api

import dev.wdona.burntout.data.api.impl.SyncPullRequest
import dev.wdona.burntout.data.api.impl.SyncResponse

interface SyncApi {
    suspend fun pull(request: SyncPullRequest): SyncResponse
}
