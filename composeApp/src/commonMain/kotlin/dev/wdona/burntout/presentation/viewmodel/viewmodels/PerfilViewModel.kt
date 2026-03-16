package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.wdona.burntout.domain.repository.UsuarioRepository
import dev.wdona.burntout.shared.domain.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



class PerfilViewModel(private val repository: UsuarioRepository) : ScreenModel {
    private val _uiState = MutableStateFlow<Usuario?>(null)
    val uiState: StateFlow<Usuario?> = _uiState.asStateFlow()

    private val _listaUsuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val listaUsuarios: StateFlow<List<Usuario>> = _listaUsuarios.asStateFlow()

    fun cargarUsuario(idUsuario: Long) {
        screenModelScope.launch {
            try {
                _uiState.value = repository.getUserById(idUsuario)
            } catch (e: Exception) {
                println("Error al cargar usuario: ${e.message}")
            }
        }
    }

    fun cargarUsuariosPorOrg(idOrg: Long) {
        screenModelScope.launch {
            _listaUsuarios.value = repository.getUsuariosByOrg(idOrg)
        }
    }

    fun actualizarPerfil(usuario: Usuario) {
        screenModelScope.launch {
            repository.actualizarUsuario(usuario)
            _uiState.value = usuario
        }
    }

    fun eliminarPerfil(idUsuario: Long) {
        screenModelScope.launch {
            repository.eliminarUsuario(idUsuario)
            _uiState.value = null
        }
    }

    fun login(username: String, contrasena: String, onResult: (Boolean) -> Unit) {
        screenModelScope.launch {
            try {
                val user = repository.login(username, contrasena)
                _uiState.value = user
                onResult(true)
            } catch (e: Exception) {
                println("Error en login: ${e.message}")
                onResult(false)
            }
        }
    }

    fun actualizarRiesgo(idUsuario: Long, riesgo: Double) {
        screenModelScope.launch {
            repository.updateRiesgoBurnout(idUsuario, riesgo)
            cargarUsuario(idUsuario)
        }
    }
}
