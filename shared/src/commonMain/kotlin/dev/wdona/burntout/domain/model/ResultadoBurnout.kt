package dev.wdona.burntout.domain.model

data class ResultadoBurnout(
    val scoreCE: Double,      // 0.0–1.0
    val scoreD: Double,       // 0.0–1.0
    val scoreRP: Double,      // 0.0–1.0 (ya invertido)
//    val nivelCE: Int,         // 0/1/2
//    val nivelD: Int,          // 0/1/2
//    val nivelRP: Int,         // 0/1/2
    val riesgoTotal: Double = (scoreD + scoreCE + scoreRP) / 3
)
