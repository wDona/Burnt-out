package dev.wdona.burntout.domain.usecase

import dev.wdona.burntout.domain.model.ResultadoBurnout
import dev.wdona.burntout.domain.model.Respuesta
import dev.wdona.burntout.shared.domain.Pregunta

class CalcularRiesgoBurnout {
    operator fun invoke(respuestas: List<Respuesta>, preguntas: List<Pregunta>): ResultadoBurnout {
        val idsCE = preguntas.filter { it.categoria == "CE" }.map { it.idPregunta }.toSet()
        val idsD  = preguntas.filter { it.categoria == "D"  }.map { it.idPregunta }.toSet()
        val idsRP = preguntas.filter { it.categoria == "RP" }.map { it.idPregunta }.toSet()
        val todasLasPreguntas = idsCE + idsD + idsRP

        if (todasLasPreguntas.isEmpty()) {
            return ResultadoBurnout(scoreCE = -1.0, scoreD = -1.0, scoreRP = -1.0, riesgoTotal = -1.0)
        }

        // Most recent valid answer per question
        val ultimasPorPregunta = respuestas
            .filter { it.respuesta >= 0 }
            .groupBy { it.idPregunta }
            .mapValues { (_, lista) -> lista.maxByOrNull { it.fecha ?: Long.MIN_VALUE }!! }

        if (!todasLasPreguntas.all { it in ultimasPorPregunta }) {
            return ResultadoBurnout(scoreCE = -1.0, scoreD = -1.0, scoreRP = -1.0, riesgoTotal = -1.0)
        }

        val calc = ultimasPorPregunta.values.toList()
        val ce = calc.filter { it.idPregunta in idsCE }.sumOf { it.respuesta.toInt() }
        val d  = calc.filter { it.idPregunta in idsD  }.sumOf { it.respuesta.toInt() }
        val rp = calc.filter { it.idPregunta in idsRP }.sumOf { it.respuesta.toInt() }

        val maxCE = idsCE.size * 6
        val maxD  = idsD.size  * 6
        val maxRP = idsRP.size * 6
        val maxTotal = maxCE + maxD + maxRP

        val scoreCE = if (maxCE > 0) ce / maxCE.toDouble() else 0.0
        val scoreD  = if (maxD  > 0) d  / maxD.toDouble()  else 0.0
        val scoreRP = if (maxRP > 0) rp / maxRP.toDouble()  else 0.0
        val riesgoTotal = (ce + d + (maxRP - rp)) / maxTotal.toDouble()

        return ResultadoBurnout(
            scoreCE = scoreCE,
            scoreD  = scoreD,
            scoreRP = scoreRP,
            riesgoTotal = riesgoTotal
        )
    }
}
