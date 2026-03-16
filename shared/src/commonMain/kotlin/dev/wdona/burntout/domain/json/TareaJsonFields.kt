package dev.wdona.burntout.domain.json

enum class TareaJsonFields(val nombreCampo: String) {
    ID("idTarea"),
    NOMBRE("titulo"),
    DESCRIPCION("descripcion"),
    ESTADO("estado"),
    ID_TABLERO("idTableroPerteneciente"),
    ID_USUARIO_ASIGNADO("idUsuarioAsignado"),
    ID_SUBTAREAS("idSubtareas")
}
