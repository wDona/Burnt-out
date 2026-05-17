package dev.wdona.burntout.presentation.ui.components.formulario

fun RespuestaMBI(valor: Int) = when (valor) {
    0 -> "Nunca / Ninguna vez"
    1 -> "Casi nunca / Pocas veces al año"
    2 -> "Algunas veces / Una vez al mes o menos"
    3 -> "Regularmente / Pocas veces al mes"
    4 -> "Bastantes veces / Una vez por semana"
    5 -> "Casi siempre / Algunas veces por semana"
    6 -> "Siempre / Todos los días"
    else -> ""
}