package dev.wdona.burntout.shared.utils

import com.russhwolf.settings.Settings

object SettingsManager {
    // TODO: Hardcodear usuario offline
    private val settings = Settings()
    private const val KEY_PRIMERA_EJECUCION = "es_primera_ejecucion"
    private const val KEY_ID_USUARIO_ACTUAL = "id_usuario_actual"
    private const val KEY_NOMBRE_USUARIO = "nombre_usuario"
    private const val KEY_ID_ORGANIZACION_ACTUAL = "id_organizacion"
    private const val KEY_ROL_ACTUAL = "id_rol"
    private const val KEY_ID_EQUIPO_ACTUAL = "id_equipo"
    private const val KEY_TOKEN_USUARIO = "token_usuario"
    private const val KEY_RIESGO_CE_USUARIO_ACTUAL = "riesgo_ce_usuario_actual"
    private const val KEY_RIESGO_D_USUARIO_ACTUAL = "riesgo_d_usuario_actual"
    private const val KEY_RIESGO_RP_USUARIO_ACTUAL = "riesgo_rp_usuario_actual"

    fun setIdUsuarioActual(id: Long?) {
        settings.putLong(KEY_ID_USUARIO_ACTUAL, id ?: Long.MIN_VALUE)
    }

    fun getIdUsuarioActual(): Long {
        val id = settings.getLong(KEY_ID_USUARIO_ACTUAL, Long.MIN_VALUE)
        return id
    }

    fun setTokenUsuario(token: String?) {
        settings.putString(KEY_TOKEN_USUARIO, token ?: "")
    }

    fun getTokenUsuario(): String {
        val token = settings.getString(KEY_TOKEN_USUARIO, "")
        return token
    }

    fun setPrimeraEjecucion(primeraEjecucion: Boolean) {
        settings.putBoolean(KEY_PRIMERA_EJECUCION, primeraEjecucion)
    }

    fun getPrimeraEjecucion(): Boolean {
        return settings.getBoolean(KEY_PRIMERA_EJECUCION, true)
    }

    fun getIdEquipoActual(): Long {
        val id = settings.getLong(KEY_ID_EQUIPO_ACTUAL, Long.MIN_VALUE)
        return id
    }

    fun getRolActual(): Long {
        val id = settings.getLong(KEY_ROL_ACTUAL, Long.MIN_VALUE)
        return id
    }

    fun getIdOrganizacionActual(): Long {
        val id = settings.getLong(KEY_ID_ORGANIZACION_ACTUAL, Long.MIN_VALUE)
        return id
    }

    fun getNombreUsuario(): String {
        val nombre = settings.getString(KEY_NOMBRE_USUARIO, "")
        return nombre
    }

    fun setNombreUsuario(nombre: String) {
        settings.putString(KEY_NOMBRE_USUARIO, nombre)
    }

    fun setIdOrganizacionActual(id: Long) {
        settings.putLong(KEY_ID_ORGANIZACION_ACTUAL, id)
    }

    fun setRolActual(id: Long) {
        settings.putLong(KEY_ROL_ACTUAL, id)
    }

    fun setIdEquipoActual(id: Long) {
        settings.putLong(KEY_ID_EQUIPO_ACTUAL, id)
    }

    fun setRiesgoCEUsuarioActual(riesgo: Double) {
        settings.putDouble(KEY_RIESGO_CE_USUARIO_ACTUAL, riesgo)
    }

    fun getRiesgoCEUsuarioActual(): Double {
        return settings.getDouble(KEY_RIESGO_CE_USUARIO_ACTUAL, 0.0)
    }

    fun setRiesgoDUsuarioActual(riesgo: Double) {
        settings.putDouble(KEY_RIESGO_D_USUARIO_ACTUAL, riesgo)
    }

    fun getRiesgoDUsuarioActual(): Double {
        return settings.getDouble(KEY_RIESGO_D_USUARIO_ACTUAL, 0.0)
    }

    fun setRiesgoRPUsuarioActual(riesgo: Double) {
        settings.putDouble(KEY_RIESGO_RP_USUARIO_ACTUAL, riesgo)
    }

    fun getRiesgoRPUsuarioActual(): Double {
        return settings.getDouble(KEY_RIESGO_RP_USUARIO_ACTUAL, 0.0)
    }
}