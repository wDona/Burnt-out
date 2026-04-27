package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.wdona.burntout.domain.repository.EquipoRepository
import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.utils.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LeaderboardUiState(
    val leaderboard: List<Equipo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val createdEquipoId: Long? = null
)

class LeaderboardViewModel(private val repository: EquipoRepository) : ScreenModel {
    private val _uiState = MutableStateFlow(LeaderboardUiState(isLoading = true))
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    fun cargarLeaderboard(idOrg: Long) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val equipos = repository.getEquiposByOrg(idOrg).sortedByDescending { it.puntuacion }
                _uiState.update { it.copy(leaderboard = equipos, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun crearEquipo(nombre: String, idOrg: Long, idUsuario: Long) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val idEquipoActual = SettingsManager.getIdEquipoActual()
                if (idEquipoActual != 0L) {
                    repository.removeUsuarioDelEquipo(idUsuario, idEquipoActual)
                }

                val nuevoEquipo = Equipo(
                    idEquipo = 0L,
                    titulo = nombre,
                    puntuacion = 0L,
                    idOrganizacion = idOrg,
                    idMiembros = listOf(idUsuario)
                )
                val idCreado = repository.crearEquipo(nuevoEquipo)
                
                SettingsManager.setIdEquipoActual(idCreado)
                
                _uiState.update { it.copy(isLoading = false, createdEquipoId = idCreado) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun resetCreatedEquipoId() {
        _uiState.update { it.copy(createdEquipoId = null) }
    }
}
