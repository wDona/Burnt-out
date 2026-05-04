package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.wdona.burntout.domain.model.OperacionPendiente
import dev.wdona.burntout.domain.repository.OperacionesPendientesRepository
import dev.wdona.burntout.domain.repository.SyncRepository
import dev.wdona.burntout.domain.usecase.RefrescarDatosUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class EstadoSync { IDLE, SINCRONIZANDO, COMPLETADO, COMPLETADO_SIN_CAMBIOS, COMPLETADO_CON_ERRORES }

class OperacionesPendientesViewModel(
    private val repository: OperacionesPendientesRepository,
    private val syncRepository: SyncRepository,
    private val refrescarDatos: RefrescarDatosUseCase
) : ScreenModel {
    // Scope propio — Voyager cancela screenModelScope al salir de PreMainScreen,
    // pero este persiste con el singleton para que sincronizarPorReconexion funcione desde cualquier pantalla.
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _uiState = MutableStateFlow<OperacionPendiente?>(null)
    val uiState = _uiState.asStateFlow()
    private val _listaOperaciones = MutableStateFlow<List<OperacionPendiente>>(emptyList())
    val listaOperaciones = _listaOperaciones

    private val _estadoSync = MutableStateFlow(EstadoSync.IDLE)
    val estadoSync: StateFlow<EstadoSync> = _estadoSync.asStateFlow()

    fun sincronizarAlIniciar() {
        appScope.launch {
            val hayPendientes = repository.getOperacionesPendientes().isNotEmpty()
            _estadoSync.value = EstadoSync.SINCRONIZANDO
            val todoOk = syncRepository.sync()
            if (todoOk) {
                refrescarDatos()
                _estadoSync.value = if (hayPendientes) EstadoSync.COMPLETADO else EstadoSync.COMPLETADO_SIN_CAMBIOS
            } else {
                _estadoSync.value = EstadoSync.COMPLETADO_CON_ERRORES
            }
        }
    }

    fun sincronizarPorReconexion() {
        if (_estadoSync.value == EstadoSync.SINCRONIZANDO) return
        appScope.launch {
            val hayPendientes = repository.getOperacionesPendientes().isNotEmpty()
            _estadoSync.value = EstadoSync.SINCRONIZANDO
            val todoOk = syncRepository.sync()
            if (todoOk) {
                refrescarDatos()
                _estadoSync.value = if (hayPendientes) EstadoSync.COMPLETADO else EstadoSync.COMPLETADO_SIN_CAMBIOS
            } else {
                _estadoSync.value = EstadoSync.COMPLETADO_CON_ERRORES
            }
        }
    }
}
