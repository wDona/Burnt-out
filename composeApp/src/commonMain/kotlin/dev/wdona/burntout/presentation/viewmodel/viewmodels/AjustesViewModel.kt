package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.wdona.burntout.AppInfo
import dev.wdona.burntout.domain.model.Ajuste
import dev.wdona.burntout.domain.repository.AjusteRepository
import dev.wdona.burntout.shared.domain.Usuario
import dev.wdona.burntout.shared.utils.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AjustesUiState(
    val esPrimeraEjecucion: Boolean = true,
    val token: String = "",
    val idUsuario: Long = Long.MIN_VALUE,
    val idOrganizacion: Long = Long.MIN_VALUE,
    val idEquipo: Long = Long.MIN_VALUE,
    val nombreUsuario: String = "Offline",
    val versionApp: String = AppInfo.version,
    // w.x.yz -> w. major version, x. centena de commits, yz. -> decena/ud de commit
)

class AjustesViewModel(private val repository: AjusteRepository) : ScreenModel {
    private val _ajustesUiState = MutableStateFlow(
        AjustesUiState(
            esPrimeraEjecucion = SettingsManager.getPrimeraEjecucion(),
            token = SettingsManager.getTokenUsuario(),
            idUsuario = SettingsManager.getIdUsuarioActual(),
            idOrganizacion = SettingsManager.getIdOrganizacionActual(),
            idEquipo = SettingsManager.getIdEquipoActual(),
            nombreUsuario = SettingsManager.getNombreUsuario(),
        )
    )

    val ajustesUiState = _ajustesUiState.asStateFlow()

    // Deprecated??
    var _listaAjustes = MutableStateFlow<List<Ajuste?>>(emptyList())
    val listaAjustes = _listaAjustes.asStateFlow()
    var _uiStateUsuarioActual = MutableStateFlow<Usuario?>(null)
    val uiStateUsuarioActual = _uiStateUsuarioActual.asStateFlow()

    fun cargarAjustesUsuarioActual() {
        assert(uiStateUsuarioActual.value != null)

        screenModelScope.launch {
            _listaAjustes.value = repository.getAjustesByUsuario(uiStateUsuarioActual.value!!.idUsuario)
        }
    }

    fun cargarUsuarioActual(usuario: Usuario) {
        assert(_uiStateUsuarioActual.value == null)

        _uiStateUsuarioActual.value = usuario
    }

    fun togglePrimeraEjecucion() {
        val nuevoValor = !SettingsManager.getPrimeraEjecucion()
        SettingsManager.setPrimeraEjecucion(nuevoValor)

        _ajustesUiState.value = _ajustesUiState.value.copy(esPrimeraEjecucion = nuevoValor)
    }
}
