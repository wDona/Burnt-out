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
import kotlinx.coroutines.launch



class PerfilViewModel(private val repository: UsuarioRepository) : ScreenModel {
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    val riesgoBurnoutCE = SettingsManager.getRiesgoCEUsuarioActual()
    val riesgoBurnoutD = SettingsManager.getRiesgoDUsuarioActual()
    val riesgoBurnoutRP = SettingsManager.getRiesgoRPUsuarioActual()

    private val resultadoBurnout = ResultadoBurnout(riesgoBurnoutCE, riesgoBurnoutD, riesgoBurnoutRP)

    private val _listaUsuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val listaUsuarios: StateFlow<List<Usuario>> = _listaUsuarios.asStateFlow()

    fun cargarUsuario(idUsuario: Long) {
        screenModelScope.launch {
            try {
                _usuarioActual.value = repository.getUserById(idUsuario)
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
            _usuarioActual.value = usuario
        }
    }

    fun eliminarPerfil(idUsuario: Long) {
        screenModelScope.launch {
            repository.eliminarUsuario(idUsuario)
            _usuarioActual.value = null
        }
    }

    fun login(username: String, contrasena: String, onResult: (Boolean) -> Unit) {
        screenModelScope.launch {
            try {
                val user = repository.login(username, contrasena)
                _usuarioActual.value = user
                onResult(true)
            } catch (e: Exception) {
                println("Error en login: ${e.message}")
                onResult(false)
            }
        }
    }

    fun actualizarRiesgo(idUsuario: Long) {
        screenModelScope.launch {
            repository.updateRiesgoBurnout(idUsuario, resultadoBurnout.riesgoTotal)
            cargarUsuario(idUsuario)
        }
    }
}
