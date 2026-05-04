package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.SyncApi
import dev.wdona.burntout.data.api.SyncPullRequest
import dev.wdona.burntout.data.api.SyncResponse
import dev.wdona.burntout.shared.network.ApiClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class SyncApiImpl(private val client: HttpClient = ApiClient.client) : SyncApi {
    override suspend fun pull(request: SyncPullRequest): SyncResponse {
        return client.post("sync/pull") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
