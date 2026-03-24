package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.wdona.burntout.domain.repository.EquipoRepository
import dev.wdona.burntout.shared.domain.Equipo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LeaderboardUiState(
    val leaderboard: List<Equipo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class LeaderboardViewModel(private val repository: EquipoRepository) : ScreenModel {
    private val _uiState = MutableStateFlow(LeaderboardUiState(isLoading = true))
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    fun cargarLeaderboard(idOrg: Long) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // FIXME
                val equipos = repository.getEquiposByOrg(idOrg).sortedByDescending { it.puntuacion }
                _uiState.update { it.copy(leaderboard = equipos, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
