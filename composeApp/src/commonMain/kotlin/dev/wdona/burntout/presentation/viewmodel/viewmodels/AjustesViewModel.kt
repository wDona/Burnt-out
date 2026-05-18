package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.wdona.burntout.platform.AppInfo
import dev.wdona.burntout.domain.model.Ajuste
import dev.wdona.burntout.domain.repository.AjusteRepository
import dev.wdona.burntout.domain.repository.UsuarioRepository
import dev.wdona.burntout.shared.domain.Usuario
import dev.wdona.burntout.shared.utils.SettingsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

data class AjustesUiState(
    val primerCuestionarioHecho: Boolean = false,
    val token: String = "",
    val idUsuario: Long = Long.MIN_VALUE,
    val idOrganizacion: Long = Long.MIN_VALUE,
    val idEquipo: Long = Long.MIN_VALUE,
    val nombreUsuario: String = "Invitado",
    val versionApp: String = AppInfo.version,
    val hoyHecho: Boolean = false,
    val syncOk: Boolean = true,
    val respuestasAnonimas: Boolean = false
)

class AjustesViewModel(
    private val repository: AjusteRepository,
    private val usuarioRepository: UsuarioRepository,
    private val onCancelarNotificaciones: (idUsuario: Long) -> Unit = {},
    private val onReprogramarNotificaciones: (idUsuario: Long) -> Unit = {}
) : ScreenModel {
    
    val ajustesUiState = combine(
        SettingsManager.esUltimoCuestionarioHecho,
        SettingsManager.cuestionarioHoyHechoFlow,
        SettingsManager.sincronizadoEnEstaAperturaFlow,
        SettingsManager.idEquipoActualFlow,
        SettingsManager.respuestasAnonimasFlow
    ) { primerCuestionario, hoyHecho, sincronizado, idEquipoActual, anonimas ->
        AjustesUiState(
            primerCuestionarioHecho = primerCuestionario,
            hoyHecho = hoyHecho,
            token = SettingsManager.getTokenUsuario(),
            idUsuario = SettingsManager.getIdUsuarioActual(),
            idEquipo = idEquipoActual,
            idOrganizacion = SettingsManager.getIdOrganizacionActual(),
            nombreUsuario = SettingsManager.getNombreUsuario(),
            syncOk = sincronizado,
            respuestasAnonimas = anonimas
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AjustesUiState(
            primerCuestionarioHecho = SettingsManager.getPrimerCuestionarioHecho(),
            hoyHecho = SettingsManager.esCuestionarioHoyHecho(),
            token = SettingsManager.getTokenUsuario(),
            idUsuario = SettingsManager.getIdUsuarioActual(),
            idOrganizacion = SettingsManager.getIdOrganizacionActual(),
            idEquipo = SettingsManager.getIdEquipoActual(),
            nombreUsuario = SettingsManager.getNombreUsuario(),
            syncOk = SettingsManager.getSincronizadoEnEstaApertura(),
            respuestasAnonimas = SettingsManager.isRespuestasAnonimas()
        )
    )

    // Deprecated??
    var _listaAjustes = MutableStateFlow<List<Ajuste?>>(emptyList())
    val listaAjustes = _listaAjustes.asStateFlow()
    var _uiStateUsuarioActual = MutableStateFlow<Usuario?>(null)
    val uiStateUsuarioActual = _uiStateUsuarioActual.asStateFlow()

    fun cargarUsuarioActual(usuario: Usuario) {
        _uiStateUsuarioActual.value = usuario
    }

    private val _respuestasAnonimas = MutableStateFlow(SettingsManager.isRespuestasAnonimas())
    val respuestasAnonimas = _respuestasAnonimas.asStateFlow()

    private val _notificacionesActivas = MutableStateFlow(SettingsManager.isNotificacionesActivas())
    val notificacionesActivas = _notificacionesActivas.asStateFlow()

    fun toggleRespuestasAnonimas() {
        val nuevo = !_respuestasAnonimas.value
        SettingsManager.setRespuestasAnonimas(nuevo)
        _respuestasAnonimas.value = nuevo
        val idUsuario = SettingsManager.getIdUsuarioActual()
        if (idUsuario != Long.MIN_VALUE) {
            screenModelScope.launch {
                try { repository.guardarAjuste("respuestas_anonimas", nuevo.toString(), idUsuario) }
                catch (_: Exception) { }
            }
        }
    }

    fun toggleNotificacionesActivas() {
        val nuevo = !_notificacionesActivas.value
        SettingsManager.setNotificacionesActivas(nuevo)
        _notificacionesActivas.value = nuevo
        val idUsuario = SettingsManager.getIdUsuarioActual()
        screenModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            if (!nuevo) {
                try { onCancelarNotificaciones(idUsuario) } catch (_: Exception) { }
            } else if (idUsuario != Long.MIN_VALUE) {
                try { onReprogramarNotificaciones(idUsuario) } catch (_: Exception) { }
            }
            if (idUsuario != Long.MIN_VALUE) {
                try { repository.guardarAjuste("notificaciones_activas", nuevo.toString(), idUsuario) }
                catch (_: Exception) { }
            }
        }
    }

    fun resetSettings() {
        val idUsuario = SettingsManager.getIdUsuarioActual()
        val token = SettingsManager.getTokenUsuario()
        val invitado = SettingsManager.isUsuarioInvitado()
        screenModelScope.launch(Dispatchers.IO) {
            try { onCancelarNotificaciones(idUsuario) } catch (_: Exception) { }
            if (!invitado && token.isNotBlank()) {
                try { usuarioRepository.cerrarSesion(token) } catch (_: Exception) { }
            }
        }
        SettingsManager.clearAll()
    }

    private val _cuentaEliminada = MutableStateFlow(false)
    val cuentaEliminada = _cuentaEliminada.asStateFlow()

    fun eliminarCuentaPropia() {
        val idUsuario = SettingsManager.getIdUsuarioActual()
        if (idUsuario == Long.MIN_VALUE) return
        screenModelScope.launch(Dispatchers.IO) {
            try { onCancelarNotificaciones(idUsuario) } catch (_: Exception) { }
            try { usuarioRepository.eliminarUsuario(idUsuario) } catch (_: Exception) { }
            SettingsManager.clearAll()
            _cuentaEliminada.value = true
        }
    }
}
