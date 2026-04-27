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

    fun cargarTableros(idOrg: Long, idEquipo: Long) {
        screenModelScope.launch {
            _listaTableros.value = repository.getTablerosByEquipo(idOrg, idEquipo)
        }
    }

    fun cargarTableroPorId(idTablero: Long) {
        screenModelScope.launch {
            _uiState.value = repository.getTableroById(idTablero)
        }
    }

    fun crearTablero(tablero: Tablero) {
        screenModelScope.launch {
            repository.crearTablero(tablero)
            cargarTableros(tablero.idOrganizacion, SettingsManager.getIdEquipoActual())
        }
    }

    fun actualizarTablero(tablero: Tablero) {
        screenModelScope.launch {
            repository.actualizarTablero(tablero)
            cargarTableros(tablero.idOrganizacion, SettingsManager.getIdEquipoActual())
        }
    }

    fun eliminarTablero(idTablero: Long, idOrg: Long) {
        screenModelScope.launch {
            repository.eliminarTablero(idTablero)
            cargarTableros(idOrg, SettingsManager.getIdEquipoActual())
        }
    }
}
