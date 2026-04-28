package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.wdona.burntout.domain.repository.EquipoRepository
import dev.wdona.burntout.domain.usecase.AddUsuarioAlEquipoUseCase
import dev.wdona.burntout.domain.usecase.CargarMiembrosEquipo
import dev.wdona.burntout.domain.usecase.GetUsuarioByUsernameUseCase
import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.domain.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EquipoUiState(
    val equipo: Equipo? = null,
    val miembros: List<Usuario> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val userAddedSuccess: Boolean = false
)

class EquipoViewModel(
    private val repository: EquipoRepository,
    private val cargarMiembrosEquipoUseCase: CargarMiembrosEquipo,
    private val addUsuarioAlEquipoUseCase: AddUsuarioAlEquipoUseCase,
    private val getUsuarioByUsernameUseCase: GetUsuarioByUsernameUseCase
) : ScreenModel {
    private val _uiState = MutableStateFlow(EquipoUiState(isLoading = true))
    val uiState: StateFlow<EquipoUiState> = _uiState.asStateFlow()

    fun cargarEquipoPorId(idEquipo: Long) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val equipo = repository.getEquipoById(idEquipo)
                val miembros = cargarMiembrosEquipoUseCase.invoke(idEquipo)
                _uiState.update { it.copy(equipo = equipo, miembros = miembros, isLoading = false) }
            } catch (e: Exception) {
                 _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun sumarPuntos(idEquipo: Long, puntos: Long) {
        screenModelScope.launch {
            repository.updatePuntuacion(idEquipo, puntos)
            val equipo = repository.getEquipoById(idEquipo) // Refresh just the team
             _uiState.update { it.copy(equipo = equipo) }
        }
    }

    fun cargarMiembrosEquipo(idEquipo: Long) {
        screenModelScope.launch {
             _uiState.update { it.copy(isLoading = true) }
            try {
                val miembros = cargarMiembrosEquipoUseCase.invoke(idEquipo)
                _uiState.update { it.copy(miembros = miembros, isLoading = false) }
            } catch (e: Exception) {
                 println("Error cargando miembros: ${e.message}")
                 _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun addUsuarioAlEquipo(idEquipo: Long, idUsuario: Long) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, userAddedSuccess = false, error = null) }
            try {
                val success = addUsuarioAlEquipoUseCase(idEquipo, idUsuario)

                if (success) {
                    cargarMiembrosEquipo(idEquipo)
                    _uiState.update { it.copy(userAddedSuccess = true, isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Error al añadir usuario") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun anadirUsuarioAlEquipoPorNombre(idEquipo: Long, username: String) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, userAddedSuccess = false, error = null) }
            try {
                val usuarioAIntroducir = getUsuarioByUsernameUseCase(username)
                if (usuarioAIntroducir != null) {
                    val idOrgEquipo = _uiState.value.equipo?.idOrganizacion

                    if (idOrgEquipo != null && usuarioAIntroducir.idOrganizacion != idOrgEquipo) {
                        throw Exception("El usuario no pertenece a la organización del equipo")
                    }

                    val success = addUsuarioAlEquipoUseCase(idEquipo, usuarioAIntroducir.idUsuario)

                    if (success) {
                        cargarMiembrosEquipo(idEquipo)
                        _uiState.update { it.copy(userAddedSuccess = true, isLoading = false) }
                    } else {
                        throw Exception("Error al añadir usuario")
                    }
                } else {
                    throw Exception("Usuario no encontrado")
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun resetUserAddedSuccess() {
        _uiState.update { it.copy(userAddedSuccess = false) }
    }

    fun resetError() {
        _uiState.update { it.copy(error = null) }
    }

    fun salirDelEquipo(idEquipo: Long, idUsuario: Long) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val success = repository.removeUsuarioDelEquipo(idEquipo, idUsuario)
                if (success) {
                    cargarMiembrosEquipo(idEquipo)
                    _uiState.update { it.copy(isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Error al salir del equipo") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

}
