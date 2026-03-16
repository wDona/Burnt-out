package dev.wdona.burntout.shared.utils

/**
 * Generador de IDs para operaciones offline
 * Genera IDs negativos para evitar colisiones con los IDs positivos que pone el servidor
 */
object OfflineIdGenerator {
    private var lastId = 0L

    fun newId(): Long {
        lastId--
        return lastId
    }
}
