package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.wdona.burntout.domain.repository.TableroRepository
import dev.wdona.burntout.shared.domain.Tablero
import dev.wdona.burntout.shared.utils.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TablerosViewModel(private val repository: TableroRepository) : ScreenModel {
    private val _uiState = MutableStateFlow<Tablero?>(null)
    val uiState: StateFlow<Tablero?> = _uiState.asStateFlow()

    private val _listaTableros = MutableStateFlow<List<Tablero>>(emptyList())
    val listaTableros: StateFlow<List<Tablero>> = _listaTableros

    private suspend fun recargarTableros(idOrg: Long, idEquipo: Long) {
        _listaTableros.value = repository.getTablerosByEquipo(idOrg, idEquipo)
    }

    fun cargarTableros(idOrg: Long, idEquipo: Long) {
        screenModelScope.launch {
            recargarTableros(idOrg, idEquipo)
        }
    }

    fun cargarTableroPorId(idTablero: Long) {
        screenModelScope.launch {
            _uiState.value = repository.getTableroById(idTablero)
        }
    }

    fun crearTablero(tablero: Tablero, onComplete: (() -> Unit)? = null) {
        screenModelScope.launch {
            repository.crearTablero(tablero)
            recargarTableros(tablero.idOrganizacion, SettingsManager.getIdEquipoActual())
            onComplete?.invoke()
        }
    }

    fun actualizarTablero(tablero: Tablero) {
        screenModelScope.launch {
            repository.actualizarTablero(tablero)
            recargarTableros(tablero.idOrganizacion, SettingsManager.getIdEquipoActual())
        }
    }

    fun eliminarTablero(idTablero: Long, idOrg: Long) {
        screenModelScope.launch {
            repository.eliminarTablero(idTablero)
            recargarTableros(idOrg, SettingsManager.getIdEquipoActual())
        }
    }
}
