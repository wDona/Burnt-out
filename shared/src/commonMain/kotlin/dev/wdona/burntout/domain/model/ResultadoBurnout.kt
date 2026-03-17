package dev.wdona.burntout.domain.model

data class ResultadoBurnout(
    val nivelCE: Int,
    val nivelD: Int,
    val nivelRP: Int,
    val riesgoTotal: Double = (nivelCE + nivelD + nivelRP) / 6.0
)
