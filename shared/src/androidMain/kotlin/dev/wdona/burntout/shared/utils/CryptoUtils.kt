package dev.wdona.burntout.shared.utils

import java.security.MessageDigest

actual fun hashPasswordForTransport(password: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val bytes = digest.digest(password.toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
}
