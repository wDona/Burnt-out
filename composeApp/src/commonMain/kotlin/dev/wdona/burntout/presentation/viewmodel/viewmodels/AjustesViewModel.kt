package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.wdona.burntout.AppInfo
import dev.wdona.burntout.domain.model.Ajuste
import dev.wdona.burntout.domain.repository.AjusteRepository
import dev.wdona.burntout.domain.repository.UsuarioRepository
import dev.wdona.burntout.shared.domain.Usuario
import dev.wdona.burntout.shared.utils.SettingsManager
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
    private val usuarioRepository: UsuarioRepository
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

    fun cargarAjustesUsuarioActual() {
        assert(uiStateUsuarioActual.value != null)

        screenModelScope.launch {
            _listaAjustes.value = repository.getAjustesByUsuario(uiStateUsuarioActual.value!!.idUsuario)
        }
    }

    fun cargarUsuarioActual(usuario: Usuario) {
        _uiStateUsuarioActual.value = usuario
    }

    fun togglePrimeraEjecucion() {
        val nuevoValor = !SettingsManager.getPrimerCuestionarioHecho()
        SettingsManager.setPrimerCuestionarioHecho(nuevoValor)
    }

    private val _respuestasAnonimas = MutableStateFlow(SettingsManager.isRespuestasAnonimas())
    val respuestasAnonimas = _respuestasAnonimas.asStateFlow()

    fun toggleRespuestasAnonimas() {
        val nuevo = !_respuestasAnonimas.value
        SettingsManager.setRespuestasAnonimas(nuevo)
        _respuestasAnonimas.value = nuevo
    }

    fun resetSettings() {
        val token = SettingsManager.getTokenUsuario()
        val invitado = SettingsManager.isUsuarioInvitado()
        SettingsManager.clearAll()
        if (!invitado && token.isNotBlank()) {
            screenModelScope.launch {
                try { usuarioRepository.cerrarSesion(token) } catch (_: Exception) { }
            }
        }
    }

    fun salirDelEquipo() {
        screenModelScope.launch {
            val idUsuario = SettingsManager.getIdUsuarioActual()
            val idEquipo = SettingsManager.getIdEquipoActual()
            if (idEquipo != 0L && idUsuario != 0L) {
                repository.salirDelEquipo(idEquipo, idUsuario)
            }
        }
    }
}
