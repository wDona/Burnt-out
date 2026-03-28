package dev.wdona.burntout.shared.utils

import com.russhwolf.settings.Settings
import dev.wdona.burntout.shared.domain.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object SettingsManager {
    // TODO: Hardcodear usuario invitado
    private val settings = createSettings()
    private const val KEY_ID_USUARIO_ACTUAL = "id_usuario_actual"
    private const val KEY_NOMBRE_USUARIO = "nombre_usuario"
    private const val KEY_ID_ORGANIZACION_ACTUAL = "id_organizacion"
    private const val KEY_ROL_ACTUAL = "id_rol"
    private const val KEY_ID_EQUIPO_ACTUAL = "id_equipo"
    private const val KEY_TOKEN_USUARIO = "token_usuario"
    private const val KEY_RIESGO_CE_USUARIO_ACTUAL = "riesgo_ce_usuario_actual"
    private const val KEY_RIESGO_D_USUARIO_ACTUAL = "riesgo_d_usuario_actual"
    private const val KEY_RIESGO_RP_USUARIO_ACTUAL = "riesgo_rp_usuario_actual"
    private val KEY_ULTIMA_FECHA_CUESTIONARIO get() = "ultima_fecha_cuestionario_${getIdUsuarioActual()}"
    private const val KEY_SINCRONIZADO_EN_ESTA_APERTURA = "sincronizado_en_esta_apertura"
    private val KEY_PRIMER_CUESTIONARIO_HECHO get() = "cuestionario_inicial_hecho_${getIdUsuarioActual()}"

    private val _primerCuestionarioHechoFlow = MutableStateFlow(getPrimerCuestionarioHecho())
    val primerCuestionarioHechoFlow = _primerCuestionarioHechoFlow.asStateFlow()

    private val _cuestionarioHoyHechoFlow = MutableStateFlow(esCuestionarioHoyHecho())
    val cuestionarioHoyHechoFlow = _cuestionarioHoyHechoFlow.asStateFlow()

    private val _sincronizadoEnEstaAperturaFlow = MutableStateFlow(getSincronizadoEnEstaApertura())
    val sincronizadoEnEstaAperturaFlow = _sincronizadoEnEstaAperturaFlow.asStateFlow()

    private val _isAutenticadoFlow = MutableStateFlow(isAutenticado())
    val isAutenticadoFlow = _isAutenticadoFlow.asStateFlow()

    private val _idUsuarioActualFlow = MutableStateFlow(getIdUsuarioActual())
    val idUsuarioActualFlow = _idUsuarioActualFlow.asStateFlow()

    fun clearAll() {
        settings.clear()

        _primerCuestionarioHechoFlow.value = false
        _cuestionarioHoyHechoFlow.value = false
        _idUsuarioActualFlow.value = Long.MIN_VALUE
        _isAutenticadoFlow.value = false
    }

    fun setIdUsuarioActual(id: Long?) {
        val safeId = id ?: Long.MIN_VALUE
        settings.putLong(KEY_ID_USUARIO_ACTUAL, safeId)
        
        // Actualizar flujos para el nuevo usuario ANTES de que se lance isAutenticado=true
        _primerCuestionarioHechoFlow.value = getPrimerCuestionarioHecho()
        _cuestionarioHoyHechoFlow.value = esCuestionarioHoyHecho()

        _idUsuarioActualFlow.value = safeId
        _isAutenticadoFlow.value = true
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

    fun setPrimerCuestionarioHecho(primerCuestionario: Boolean) {
        settings.putBoolean(KEY_PRIMER_CUESTIONARIO_HECHO, primerCuestionario)
        _primerCuestionarioHechoFlow.value = primerCuestionario
    }

    fun getPrimerCuestionarioHecho(): Boolean {
        return settings.getBoolean(KEY_PRIMER_CUESTIONARIO_HECHO, false)
    }

    fun isUsuarioInvitado(): Boolean {
        return getIdUsuarioActual() == Long.MIN_VALUE
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
        return settings.getDouble(KEY_RIESGO_CE_USUARIO_ACTUAL, -1.0)
    }

    fun setRiesgoDUsuarioActual(riesgo: Double) {
        settings.putDouble(KEY_RIESGO_D_USUARIO_ACTUAL, riesgo)
    }

    fun getRiesgoDUsuarioActual(): Double {
        return settings.getDouble(KEY_RIESGO_D_USUARIO_ACTUAL, -1.0)
    }

    fun setRiesgoRPUsuarioActual(riesgo: Double) {
        settings.putDouble(KEY_RIESGO_RP_USUARIO_ACTUAL, riesgo)
    }

    fun getRiesgoRPUsuarioActual(): Double {
        return settings.getDouble(KEY_RIESGO_RP_USUARIO_ACTUAL, -1.0)
    }

    fun setUltimaFechaCuestionarioHoy() {
        val fechaHoy = getCurrentDateString()
        settings.putString(KEY_ULTIMA_FECHA_CUESTIONARIO, fechaHoy)
        _cuestionarioHoyHechoFlow.value = true
    }

    fun esCuestionarioHoyHecho(): Boolean {
        val ultimaFecha = settings.getString(KEY_ULTIMA_FECHA_CUESTIONARIO, "")
        val fechaHoy = getCurrentDateString()
        return ultimaFecha == fechaHoy
    }

    fun getSincronizadoEnEstaApertura(): Boolean {
        return settings.getBoolean(KEY_SINCRONIZADO_EN_ESTA_APERTURA, false)
    }

    fun setSincronizadoEnEstaApertura(sincronizado: Boolean) {
        settings.putBoolean(KEY_SINCRONIZADO_EN_ESTA_APERTURA, sincronizado)
        _sincronizadoEnEstaAperturaFlow.value = sincronizado
    }

    fun isAutenticado(): Boolean {
        return settings.hasKey(KEY_ID_USUARIO_ACTUAL)
    }

    fun setUsuarioActual(usuario: Usuario) {
        setIdUsuarioActual(usuario.idUsuario)
        setNombreUsuario(usuario.username)
        setIdEquipoActual(usuario.idEquipo)
        setIdOrganizacionActual(usuario.idOrganizacion)
        setTokenUsuario("token_${usuario.idUsuario}")
        setRolActual(1L) // FIXME
    }
}