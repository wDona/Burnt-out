package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.wdona.burntout.domain.model.ResultadoBurnout
import dev.wdona.burntout.domain.repository.UsuarioRepository
import dev.wdona.burntout.shared.domain.Usuario
import dev.wdona.burntout.shared.utils.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PerfilUiState(
    val usuario: Usuario? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class PerfilViewModel(private val repository: UsuarioRepository) : ScreenModel {
    private val _uiState = MutableStateFlow(PerfilUiState(isLoading = true))
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    val riesgoBurnoutCE = SettingsManager.getRiesgoCEUsuarioActual()
    val riesgoBurnoutD = SettingsManager.getRiesgoDUsuarioActual()
    val riesgoBurnoutRP = SettingsManager.getRiesgoRPUsuarioActual()

    private val resultadoBurnout = ResultadoBurnout(riesgoBurnoutCE, riesgoBurnoutD, riesgoBurnoutRP)

    fun cargarUsuario(idUsuario: Long) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val user = repository.getUserById(idUsuario)
                _uiState.update { it.copy(usuario = user, isLoading = false) }
            } catch (e: Exception) {
                println("Error al cargar usuario: ${e.message}")
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }


    fun actualizarPerfil(usuario: Usuario) {
        screenModelScope.launch {
            repository.actualizarUsuario(usuario)
            _uiState.update { it.copy(usuario = usuario) }
        }
    }

    fun eliminarPerfil(idUsuario: Long) {
        screenModelScope.launch {
            repository.eliminarUsuario(idUsuario)
            _uiState.update { it.copy(usuario = null) }
        }
    }

    fun login(username: String, contrasena: String, onResult: (Boolean) -> Unit) {
        screenModelScope.launch {
            try {
                val user = repository.login(username, contrasena).usuario
                _uiState.update { it.copy(usuario = user) }
                onResult(true)
            } catch (e: Exception) {
                println("Error en login: ${e.message}")
                onResult(false)
            }
        }
    }

    fun actualizarRiesgo(idUsuario: Long) {
        screenModelScope.launch {
            // repository.updateRiesgoBurnout(idUsuario, resultadoBurnout.riesgoTotal)
            cargarUsuario(idUsuario)
        }
    }
}
