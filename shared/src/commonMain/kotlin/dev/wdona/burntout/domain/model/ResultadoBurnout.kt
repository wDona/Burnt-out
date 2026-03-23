package dev.wdona.burntout.domain.model

data class ResultadoBurnout(
    val nivelCE: Double,
    val nivelD: Double,
    val nivelRP: Double,
    val riesgoTotal: Double = (nivelCE + nivelD + nivelRP) / 6.0
)
