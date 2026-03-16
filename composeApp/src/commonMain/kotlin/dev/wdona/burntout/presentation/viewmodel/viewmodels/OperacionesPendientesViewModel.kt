package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import dev.wdona.burntout.domain.model.OperacionPendiente
import dev.wdona.burntout.domain.repository.OperacionesPendientesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class OperacionesPendientesViewModel(private val repository: OperacionesPendientesRepository) : ScreenModel {
    private val _uiState = MutableStateFlow<OperacionPendiente?>(null)
    val uiState = _uiState.asStateFlow()
    private val _listaOperaciones = MutableStateFlow<List<OperacionPendiente>>(emptyList())
    val listaOperaciones = _listaOperaciones
}
