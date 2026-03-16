package dev.wdona.burntout.domain.entity

enum class Entity(private val customNombre: String? = null) {
    USUARIO("UsuarioEntity"),
    ORGANIZACION("OrganizacionEntity"),
    AJUSTE("AjusteEntity"),
    TABLERO("TableroEntity"),
    EQUIPO("EquipoEntity"),
    PREGUNTA("PreguntaEntity"),
    TAREA("TareaEntity"),
    SUBTAREA("SubtareaEntity"),
    USER_TEAM("User_TeamEntity"),
    AJUSTE_USER("Ajuste_UserEntity"),
    RESPONDER("ResponderEntity"),
    OPERACIONPENDIENTE("OperacionPendienteEntity");

    fun getNombreEntity(): String {
        return customNombre ?: this.name
    }
}
