package dev.wdona.burntout.shared.network

import dev.wdona.burntout.API_PATH
import dev.wdona.burntout.SERVER_PORT
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object ApiClient {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            })
        }

        expectSuccess = true

        install(HttpTimeout) {
            requestTimeoutMillis = 15000 
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }

        defaultRequest {
            val host = dev.wdona.burntout.shared.utils.SettingsManager.getHostActual()
            url("http://$host:$SERVER_PORT/$API_PATH/")
            contentType(ContentType.Application.Json)
            val token = dev.wdona.burntout.shared.utils.SettingsManager.getTokenUsuario()
            if (token.isNotBlank()) {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }
}
