package dev.wdona.burntout.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class Equipo(
    val idEquipo: Long,
    val titulo: String,
    val puntuacion: Long?,
    val idOrganizacion: Long,
    val idMiembros: List<Long>,
    val isDeleted: Boolean = false,
    val updatedAt: Long = 0L
) {
    val nivel: Int
        get() {
            var n = 0
            var rest = puntuacion ?: 0L
            while (true) {
                val cost = (n + 1) * 1000L
                if (rest >= cost) {
                    rest -= cost
                    n++
                } else {
                    break
                }
            }
            return n
        }

    val puntosRestantes: Long
        get() {
            var n = 0
            var rest = puntuacion ?: 0L
            while (true) {
                val cost = (n + 1) * 1000L
                if (rest >= cost) {
                    rest -= cost
                    n++
                } else {
                    break
                }
            }
            return rest
        }

    val costoSiguienteNivel: Long
        get() = (nivel + 1) * 1000L
}
