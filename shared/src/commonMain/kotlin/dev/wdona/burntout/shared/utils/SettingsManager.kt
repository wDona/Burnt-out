package dev.wdona.burntout.shared.utils

import dev.wdona.burntout.shared.domain.Usuario
import dev.wdona.burntout.shared.network.HOST
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object SettingsManager {
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
    private const val KEY_SINCRONIZADO_EN_ESTA_APERTURA = "sincronizado_en_esta_apertura"
    private const val KEY_RESPUESTAS_ANONIMAS = "respuestas_anonimas"
    private const val KEY_HOST_PERSONALIZADO = "host_personalizado"
    private const val KEY_USAR_HOST_PERSONALIZADO = "usar_host_personalizado"

    private val KEY_LAST_SYNC_TIMESTAMP get() = "last_sync_timestamp_${_idUsuarioActualFlow.value}"

    private val _idUsuarioActualFlow = MutableStateFlow(settings.getLong(KEY_ID_USUARIO_ACTUAL, Long.MIN_VALUE))
    val idUsuarioActualFlow = _idUsuarioActualFlow.asStateFlow()

    private val _nombreUsuarioFlow = MutableStateFlow(settings.getString(KEY_NOMBRE_USUARIO, ""))
    val nombreUsuarioFlow = _nombreUsuarioFlow.asStateFlow()

    private val _idEquipoActualFlow = MutableStateFlow(settings.getLong(KEY_ID_EQUIPO_ACTUAL, Long.MIN_VALUE))
    val idEquipoActualFlow = _idEquipoActualFlow.asStateFlow()

    private val _idOrganizacionActualFlow = MutableStateFlow(settings.getLong(KEY_ID_ORGANIZACION_ACTUAL, Long.MIN_VALUE))
    val idOrganizacionActualFlow = _idOrganizacionActualFlow.asStateFlow()

    private val _rolActualFlow = MutableStateFlow(settings.getLong(KEY_ROL_ACTUAL, Long.MIN_VALUE))
    val rolActualFlow = _rolActualFlow.asStateFlow()

    private val _tokenUsuarioFlow = MutableStateFlow(settings.getString(KEY_TOKEN_USUARIO, ""))
    val tokenUsuarioFlow = _tokenUsuarioFlow.asStateFlow()

    private val _isAutenticadoFlow = MutableStateFlow(settings.hasKey(KEY_ID_USUARIO_ACTUAL))
    val isAutenticadoFlow = _isAutenticadoFlow.asStateFlow()

    private val _sincronizadoEnEstaAperturaFlow = MutableStateFlow(settings.getBoolean(KEY_SINCRONIZADO_EN_ESTA_APERTURA, false))
    val sincronizadoEnEstaAperturaFlow = _sincronizadoEnEstaAperturaFlow.asStateFlow()

    private val _respuestasAnonimasFlow = MutableStateFlow(settings.getBoolean(KEY_RESPUESTAS_ANONIMAS, false))
    val respuestasAnonimasFlow = _respuestasAnonimasFlow.asStateFlow()

    private val _lastSyncTimestampFlow = MutableStateFlow(settings.getLong("last_sync_timestamp_${settings.getLong(KEY_ID_USUARIO_ACTUAL, Long.MIN_VALUE)}", 0L))
    val lastSyncTimestampFlow = _lastSyncTimestampFlow.asStateFlow()

    private val KEY_ULTIMA_FECHA_CUESTIONARIO get() = "ultima_fecha_cuestionario_${_idUsuarioActualFlow.value}"
    private val KEY_PRIMER_CUESTIONARIO_HECHO get() = "cuestionario_inicial_hecho_${_idUsuarioActualFlow.value}"

    private val _esUltimoCuestionarioHecho = MutableStateFlow(settings.getBoolean("cuestionario_inicial_hecho_${settings.getLong(KEY_ID_USUARIO_ACTUAL, Long.MIN_VALUE)}", false))
    val esUltimoCuestionarioHecho = _esUltimoCuestionarioHecho.asStateFlow()

    private val _cuestionarioHoyHechoFlow = MutableStateFlow(false)
    val cuestionarioHoyHechoFlow = _cuestionarioHoyHechoFlow.asStateFlow()

    init {
        _cuestionarioHoyHechoFlow.value = checkCuestionarioHoyHecho()
    }

    private fun checkCuestionarioHoyHecho(idUsuario: Long = _idUsuarioActualFlow.value): Boolean {
        val key = "ultima_fecha_cuestionario_$idUsuario"
        val ultimaFecha = settings.getString(key, "")
        val fechaHoy = getCurrentDateString()
        return ultimaFecha == fechaHoy
    }

    fun getIdUsuarioActual(): Long = _idUsuarioActualFlow.value
    fun getNombreUsuario(): String = _nombreUsuarioFlow.value
    fun getIdEquipoActual(): Long = _idEquipoActualFlow.value
    fun getIdOrganizacionActual(): Long = _idOrganizacionActualFlow.value
    fun getRolActual(): Long = _rolActualFlow.value
    fun getTokenUsuario(): String = _tokenUsuarioFlow.value

    fun setPrimerCuestionarioHecho(primerCuestionario: Boolean) {
        settings.putBoolean(KEY_PRIMER_CUESTIONARIO_HECHO, primerCuestionario)
        _esUltimoCuestionarioHecho.value = primerCuestionario
    }

    fun getPrimerCuestionarioHecho(): Boolean {
        return settings.getBoolean(KEY_PRIMER_CUESTIONARIO_HECHO, false)
    }

    fun esCuestionarioHoyHecho(): Boolean {
        val isHoyHecho = checkCuestionarioHoyHecho()
        if (_cuestionarioHoyHechoFlow.value != isHoyHecho) {
            _cuestionarioHoyHechoFlow.value = isHoyHecho
        }
        return isHoyHecho
    }
    fun getSincronizadoEnEstaApertura(): Boolean = _sincronizadoEnEstaAperturaFlow.value
    fun isRespuestasAnonimas(): Boolean = _respuestasAnonimasFlow.value
    fun isAdminOrOwner(): Boolean = _rolActualFlow.value >= 1L

    fun getLastSyncTimestamp(): Long = _lastSyncTimestampFlow.value
    fun setLastSyncTimestamp(timestamp: Long) {
        settings.putLong(KEY_LAST_SYNC_TIMESTAMP, timestamp)
        _lastSyncTimestampFlow.value = timestamp
    }

    fun setRespuestasAnonimas(anonimas: Boolean) {
        settings.putBoolean(KEY_RESPUESTAS_ANONIMAS, anonimas)
        _respuestasAnonimasFlow.value = anonimas
    }
    fun isAutenticado(): Boolean = _isAutenticadoFlow.value
    fun isUsuarioInvitado(): Boolean = getIdUsuarioActual() == Long.MIN_VALUE

    fun getHostPersonalizado(): String = settings.getString(KEY_HOST_PERSONALIZADO, "")
    fun setHostPersonalizado(host: String) = settings.putString(KEY_HOST_PERSONALIZADO, host)
    fun isUsandoHostPersonalizado(): Boolean = settings.getBoolean(KEY_USAR_HOST_PERSONALIZADO, false)
    fun setUsarHostPersonalizado(usar: Boolean) = settings.putBoolean(KEY_USAR_HOST_PERSONALIZADO, usar)

    fun getHostActual(): String {
        return if (isUsandoHostPersonalizado()) getHostPersonalizado().ifBlank { HOST } else HOST
    }

    fun setTokenUsuario(token: String?) {
        val safeToken = token ?: ""
        settings.putString(KEY_TOKEN_USUARIO, safeToken)
        _tokenUsuarioFlow.value = safeToken
    }

    fun setIdOrganizacionActual(id: Long) {
        settings.putLong(KEY_ID_ORGANIZACION_ACTUAL, id)
        _idOrganizacionActualFlow.value = id
    }

    fun setRolActual(id: Long) {
        settings.putLong(KEY_ROL_ACTUAL, id)
        _rolActualFlow.value = id
    }

    fun setIdEquipoActual(id: Long) {
        settings.putLong(KEY_ID_EQUIPO_ACTUAL, id)
        _idEquipoActualFlow.value = id
    }

    fun setSincronizadoEnEstaApertura(sincronizado: Boolean) {
        settings.putBoolean(KEY_SINCRONIZADO_EN_ESTA_APERTURA, sincronizado)
        _sincronizadoEnEstaAperturaFlow.value = sincronizado
    }

    fun setUltimaFechaCuestionarioHoy() {
        val fechaHoy = getCurrentDateString()
        settings.putString(KEY_ULTIMA_FECHA_CUESTIONARIO, fechaHoy)
        _cuestionarioHoyHechoFlow.value = true
    }

    fun setRiesgoCEUsuarioActual(riesgo: Double) = settings.putDouble(KEY_RIESGO_CE_USUARIO_ACTUAL, riesgo)
    fun getRiesgoCEUsuarioActual(): Double = settings.getDouble(KEY_RIESGO_CE_USUARIO_ACTUAL, -1.0)
    fun setRiesgoDUsuarioActual(riesgo: Double) = settings.putDouble(KEY_RIESGO_D_USUARIO_ACTUAL, riesgo)
    fun getRiesgoDUsuarioActual(): Double = settings.getDouble(KEY_RIESGO_D_USUARIO_ACTUAL, -1.0)
    fun setRiesgoRPUsuarioActual(riesgo: Double) = settings.putDouble(KEY_RIESGO_RP_USUARIO_ACTUAL, riesgo)
    fun getRiesgoRPUsuarioActual(): Double = settings.getDouble(KEY_RIESGO_RP_USUARIO_ACTUAL, -1.0)

    fun setUsuarioActual(usuario: Usuario) {
        settings.putString(KEY_NOMBRE_USUARIO, usuario.username)
        _nombreUsuarioFlow.value = usuario.username
        
        setIdEquipoActual(usuario.idEquipo)
        setIdOrganizacionActual(usuario.idOrganizacion)
        setRolActual(when (usuario.rol) { "OWNER" -> 2L; "ADMIN" -> 1L; else -> 0L })
        
        settings.putLong(KEY_ID_USUARIO_ACTUAL, usuario.idUsuario)
        _idUsuarioActualFlow.value = usuario.idUsuario
        
        _lastSyncTimestampFlow.value = getLastSyncTimestamp()
        _isAutenticadoFlow.value = true
        _esUltimoCuestionarioHecho.value = settings.getBoolean(KEY_PRIMER_CUESTIONARIO_HECHO, false)
        _cuestionarioHoyHechoFlow.value = checkCuestionarioHoyHecho()
    }

    fun setUsuarioInvitado() {
        val invitadoName = "Invitado"
        settings.putString(KEY_NOMBRE_USUARIO, invitadoName)
        _nombreUsuarioFlow.value = invitadoName
        
        setIdEquipoActual(Long.MIN_VALUE)
        setIdOrganizacionActual(Long.MIN_VALUE)
        setTokenUsuario("token_invitado")
        setRolActual(0L)
        
        settings.putLong(KEY_ID_USUARIO_ACTUAL, Long.MIN_VALUE)
        _idUsuarioActualFlow.value = Long.MIN_VALUE
        
        _lastSyncTimestampFlow.value = getLastSyncTimestamp()
        _isAutenticadoFlow.value = true
    }

    fun clearAll() {
        val hostPersonalizado = getHostPersonalizado()
        val usarHostPersonalizado = isUsandoHostPersonalizado()
        settings.clear()
        if (hostPersonalizado.isNotBlank()) setHostPersonalizado(hostPersonalizado)
        if (usarHostPersonalizado) setUsarHostPersonalizado(true)
        _idUsuarioActualFlow.value = Long.MIN_VALUE
        _nombreUsuarioFlow.value = ""
        _idEquipoActualFlow.value = Long.MIN_VALUE
        _idOrganizacionActualFlow.value = Long.MIN_VALUE
        _rolActualFlow.value = Long.MIN_VALUE
        _tokenUsuarioFlow.value = ""
        _isAutenticadoFlow.value = false
        _sincronizadoEnEstaAperturaFlow.value = false
        _lastSyncTimestampFlow.value = 0L
        _esUltimoCuestionarioHecho.value = false
        _cuestionarioHoyHechoFlow.value = false
    }
}
