package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.wdona.burntout.domain.model.OperacionPendiente
import dev.wdona.burntout.domain.repository.OperacionesPendientesRepository
import dev.wdona.burntout.domain.usecase.RefrescarDatosUseCase
import dev.wdona.burntout.domain.usecase.SincronizarPendientesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class EstadoSync { IDLE, SINCRONIZANDO, COMPLETADO, COMPLETADO_CON_ERRORES }

class OperacionesPendientesViewModel(
    private val repository: OperacionesPendientesRepository,
    private val sincronizarPendientes: SincronizarPendientesUseCase,
    private val refrescarDatos: RefrescarDatosUseCase
) : ScreenModel {
    private val _uiState = MutableStateFlow<OperacionPendiente?>(null)
    val uiState = _uiState.asStateFlow()
    private val _listaOperaciones = MutableStateFlow<List<OperacionPendiente>>(emptyList())
    val listaOperaciones = _listaOperaciones

    private val _estadoSync = MutableStateFlow(EstadoSync.IDLE)
    val estadoSync: StateFlow<EstadoSync> = _estadoSync.asStateFlow()

    fun sincronizarAlIniciar() {
        screenModelScope.launch {
            _estadoSync.value = EstadoSync.SINCRONIZANDO
            val todoOk = sincronizarPendientes()
            if (todoOk) {
                refrescarDatos()
                _estadoSync.value = EstadoSync.COMPLETADO
            } else {
                _estadoSync.value = EstadoSync.COMPLETADO_CON_ERRORES
            }
        }
    }
}
