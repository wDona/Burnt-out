package dev.wdona.burntout.domain.repository

interface SyncRepository {
    suspend fun sync() : Boolean
}
