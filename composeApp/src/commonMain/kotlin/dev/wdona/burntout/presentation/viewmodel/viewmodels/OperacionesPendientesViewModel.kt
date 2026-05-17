package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import dev.wdona.burntout.domain.model.OperacionPendiente
import dev.wdona.burntout.domain.model.RateLimitedException
import dev.wdona.burntout.domain.repository.OperacionesPendientesRepository
import dev.wdona.burntout.domain.repository.SyncRepository
import dev.wdona.burntout.domain.usecase.RefrescarDatosUseCase
import dev.wdona.burntout.shared.utils.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class EstadoSync { IDLE, SINCRONIZANDO, COMPLETADO, COMPLETADO_SIN_CAMBIOS, CON_ERRORES, CON_ERRORES_RECONECTAR, RATE_LIMITED }

class OperacionesPendientesViewModel(
    private val repository: OperacionesPendientesRepository,
    private val syncRepository: SyncRepository,
    private val refrescarDatos: RefrescarDatosUseCase
) : ScreenModel {
    // Scope propio - Voyager cancela screenModelScope al salir de PreMainScreen,
    // pero este persiste con el singleton para que sincronizarPorReconexion funcione desde cualquier pantalla.
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _uiState = MutableStateFlow<OperacionPendiente?>(null)
    val uiState = _uiState.asStateFlow()
    private val _listaOperaciones = MutableStateFlow<List<OperacionPendiente>>(emptyList())
    val listaOperaciones = _listaOperaciones

    private val _estadoSync = MutableStateFlow(EstadoSync.IDLE)
    val estadoSync: StateFlow<EstadoSync> = _estadoSync.asStateFlow()

    private val _syncTick = MutableStateFlow(0L)
    val syncTick: StateFlow<Long> = _syncTick.asStateFlow()

    fun sincronizarAlIniciar() {
        if (SettingsManager.isUsuarioInvitado()) {
            _estadoSync.value = EstadoSync.COMPLETADO_SIN_CAMBIOS
            return
        }
        appScope.launch {
            val hayPendientes = repository.getOperacionesPendientes().isNotEmpty()
            _estadoSync.value = EstadoSync.SINCRONIZANDO
            try {
                val todoOk = syncRepository.sync()
                if (todoOk) {
                    refrescarDatos()
                    _syncTick.value++
                    _estadoSync.value = if (hayPendientes) EstadoSync.COMPLETADO else EstadoSync.COMPLETADO_SIN_CAMBIOS
                } else {
                    _estadoSync.value = EstadoSync.CON_ERRORES
                }
            } catch (_: RateLimitedException) {
                _estadoSync.value = EstadoSync.RATE_LIMITED
            }
        }
    }

    fun sincronizarPorReconexion() {
        if (SettingsManager.isUsuarioInvitado()) return
        if (_estadoSync.value == EstadoSync.SINCRONIZANDO) return
        appScope.launch {
            val hayPendientes = repository.getOperacionesPendientes().isNotEmpty()
            _estadoSync.value = EstadoSync.SINCRONIZANDO
            try {
                val todoOk = syncRepository.sync()
                if (todoOk) {
                    refrescarDatos()
                    _syncTick.value++
                    _estadoSync.value = if (hayPendientes) EstadoSync.COMPLETADO else EstadoSync.COMPLETADO_SIN_CAMBIOS
                } else {
                    _estadoSync.value = EstadoSync.CON_ERRORES_RECONECTAR
                }
            } catch (_: RateLimitedException) {
                _estadoSync.value = EstadoSync.RATE_LIMITED
            }
        }
    }

    fun resetEstadoSync() {
        _estadoSync.value = EstadoSync.IDLE
    }
}
