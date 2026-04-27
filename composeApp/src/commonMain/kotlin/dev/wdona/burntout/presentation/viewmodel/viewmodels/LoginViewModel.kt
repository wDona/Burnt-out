package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.wdona.burntout.domain.repository.UsuarioRepository
import dev.wdona.burntout.shared.domain.RegistroRequest
import dev.wdona.burntout.shared.domain.Usuario
import dev.wdona.burntout.shared.utils.OfflineIdGenerator
import dev.wdona.burntout.shared.utils.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    var isLogin: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class LoginViewModel(private val usuarioRepository: UsuarioRepository) : ScreenModel {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun clearSuccess() {
        _uiState.update { it.copy(success = false, error = null) }
    }

    fun toggleMode() {
        _uiState.update { it.copy(isLogin = !it.isLogin) }
    }

    fun login(username: String, contrasena: String, settingsViewModel: AjustesViewModel) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val usuario = usuarioRepository.login(username, contrasena)
                SettingsManager.setUsuarioActual(usuario)
                settingsViewModel.cargarUsuarioActual(usuario)

                _uiState.update { it.copy(isLoading = false, success = true) }
            } catch (e: Exception) {
                _uiState.update { 
                    val msg = e.message ?: "Error desconocido"
                    val errorMsg = if (msg.contains("401")) "Usuario o contraseña incorrectos" else "Error al iniciar sesión: $msg"
                    it.copy(isLoading = false, error = errorMsg) 
                }
            }
        }
    }

    fun register(
        username: String,
        contrasena: String,
        nombre: String,
        settingsViewModel: AjustesViewModel,
        modo: String = "CREAR_ORG",
        nombreOrg: String? = null,
        codigoInvitacion: String? = null
    ) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val request = RegistroRequest(
                    username = username,
                    password = contrasena,
                    nombre = nombre,
                    modo = modo,
                    nombreOrg = nombreOrg ?: "Org de $nombre",
                    codigoInvitacion = codigoInvitacion
                )
                val usuarioRegistrado = usuarioRepository.registrar(request)

                SettingsManager.setUsuarioActual(usuarioRegistrado)
                settingsViewModel.cargarUsuarioActual(usuarioRegistrado)
                SettingsManager.setPrimerCuestionarioHecho(false)

                _uiState.update { it.copy(isLoading = false, success = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al registrar: ${e.message}") }
            }
        }
    }


}
