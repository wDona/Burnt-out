package dev.wdona.burntout.domain.usecase

import dev.wdona.burntout.domain.model.ResultadoBurnout
import dev.wdona.burntout.domain.model.Respuesta

class CalcularRiesgoBurnout {
    operator fun invoke(respuestas: List<Respuesta>): ResultadoBurnout {
        val itemsCE = setOf(1L, 2L, 3L, 6L, 8L, 13L, 14L, 16L, 20L)
        val itemsD = setOf(5L, 10L, 11L, 15L, 22L)
        val itemsRP = setOf(4L, 7L, 9L, 12L, 17L, 18L, 19L, 21L)

        val ce = respuestas.filter { it.idPregunta in itemsCE }.sumOf { it.respuesta.toInt() }
        val d = respuestas.filter { it.idPregunta in itemsD }.sumOf { it.respuesta.toInt() }
        val rp = respuestas.filter { it.idPregunta in itemsRP }.sumOf { it.respuesta.toInt() }

        val scoreCE = ce / 54.0                  // max 9×6
        val scoreD  = d  / 30.0                  // max 5×6
        val scoreRP = 1.0 - (rp / 48.0)          // max 8×6 - invertido

        return ResultadoBurnout(
            scoreCE = scoreCE,
            scoreD  = scoreD,
            scoreRP = scoreRP,
//            nivelCE  = when { ce >= 27 -> 2; ce >= 19 -> 1; else -> 0 },
//            nivelD   = when { d  >= 10 -> 2; d  >=  6 -> 1; else -> 0 },
//            nivelRP  = when { rp <= 33 -> 2; rp <= 39 -> 1; else -> 0 },
        )
    }
}