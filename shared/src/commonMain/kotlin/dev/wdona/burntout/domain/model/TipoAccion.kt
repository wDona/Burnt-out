package dev.wdona.burntout.domain.model

enum class TipoAccion(
    private val accion: String
) {
    CREACION("CREAR"),
    ACTUALIZACION("ACTUALIZAR"),
    ELIMINACION("ELIMINAR");

    fun getNombreAccion(): String {
        return accion
    }
}
