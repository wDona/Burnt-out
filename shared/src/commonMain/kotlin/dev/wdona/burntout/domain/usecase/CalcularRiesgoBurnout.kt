package dev.wdona.burntout.domain.usecase

import dev.wdona.burntout.domain.model.ResultadoBurnout
import dev.wdona.burntout.shared.domain.Respuesta

class CalcularRiesgoBurnout {
    operator fun invoke(respuestas: List<Respuesta>): ResultadoBurnout {
        val itemsCE = setOf(1L, 2L, 3L, 6L, 8L, 13L, 14L, 16L, 20L)
        val itemsD = setOf(5L, 10L, 11L, 15L, 22L)
        val itemsRP = setOf(4L, 7L, 9L, 12L, 17L, 18L, 19L, 21L)

        val sumCE = respuestas.filter { it.idPregunta in itemsCE }.sumOf { it.respuesta.toInt() }
        val sumD = respuestas.filter { it.idPregunta in itemsD }.sumOf { it.respuesta.toInt() }
        val sumRP = respuestas.filter { it.idPregunta in itemsRP }.sumOf { it.respuesta.toInt() }

        // Cansancio emocional
        val nivelCE = when {
            sumCE >= 27 -> 2
            sumCE >= 19 -> 1
            else -> 0
        }

        // Despersonalización
        val nivelD = when {
            sumD >= 10 -> 2
            sumD >= 6 -> 1
            else -> 0
        }

        // Realizacion personal
        val nivelRP = when {
            sumRP <= 33 -> 2
            sumRP <= 39 -> 1
            else -> 0
        }

        return ResultadoBurnout(nivelCE, nivelD, nivelRP)
    }
}