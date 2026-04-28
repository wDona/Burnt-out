package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.wdona.burntout.data.api.InvitacionApi
import dev.wdona.burntout.data.api.impl.InvitacionApiImpl
import dev.wdona.burntout.domain.repository.EquipoRepository
import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.domain.GenerarInvitacionRequest
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
    val createdEquipoId: Long? = null,
    val invitacionCode: String? = null,
    val isGenerandoInvitacion: Boolean = false,
    val invitacionError: String? = null
)

class LeaderboardViewModel(
    private val repository: EquipoRepository,
    private val invitacionApi: InvitacionApi = InvitacionApiImpl()
) : ScreenModel {
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

    fun generarInvitacion(idUsuario: Long, rol: String) {
        screenModelScope.launch {
            _uiState.update { it.copy(isGenerandoInvitacion = true, invitacionError = null, invitacionCode = null) }
            try {
                val result = invitacionApi.generarCodigo(GenerarInvitacionRequest(idUsuarioAdmin = idUsuario, rol = rol))
                _uiState.update { it.copy(isGenerandoInvitacion = false, invitacionCode = result.code) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isGenerandoInvitacion = false, invitacionError = e.message) }
            }
        }
    }

    fun clearInvitacion() {
        _uiState.update { it.copy(invitacionCode = null, invitacionError = null) }
    }
}
