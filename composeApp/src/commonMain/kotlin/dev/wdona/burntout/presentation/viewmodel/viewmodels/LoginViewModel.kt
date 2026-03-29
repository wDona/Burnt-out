package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.wdona.burntout.domain.repository.UsuarioRepository
import dev.wdona.burntout.shared.domain.Usuario
import dev.wdona.burntout.shared.utils.OfflineIdGenerator
import dev.wdona.burntout.shared.utils.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLogin: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class LoginViewModel(private val usuarioRepository: UsuarioRepository) : ScreenModel {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun toggleMode() {
        _uiState.update { it.copy(isLogin = !it.isLogin) }
    }

    fun login(username: String, contrasena: String) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val usuario = usuarioRepository.login(username, contrasena)
                SettingsManager.setUsuarioActual(usuario)
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

    fun register(username: String, contrasena: String, nombre: String) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val existe = usuarioRepository.existeUsuario(username)
                if (existe) {
                    _uiState.update { it.copy(isLoading = false, error = "El usuario ya existe") }
                    return@launch
                }

                val nuevoUsuario = Usuario(
                    idUsuario = OfflineIdGenerator.newId(),
                    username = username,
                    password = contrasena,
                    nombre = nombre,
                    riesgoBurnout = 0.0,
                    descripcion = "",
                    idOrganizacion = 1L, // Default
                    idEquipo = 0L
                )
                
                usuarioRepository.crearUsuario(nuevoUsuario)
                
                // Login automatico tras registro
                val usuarioLogeado = usuarioRepository.login(username, contrasena)
                
                SettingsManager.setUsuarioActual(usuarioLogeado)
                SettingsManager.setPrimerCuestionarioHecho(false)

                _uiState.update { it.copy(isLoading = false, success = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al registrar: ${e.message}") }
            }
        }
    }


}
