package dev.wdona.burntout.shared.utils

import java.security.MessageDigest

// Implementado con actual para poder usarlo en iOS en el futuro
actual fun hashPasswordForTransport(password: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val bytes = digest.digest(password.toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
}
