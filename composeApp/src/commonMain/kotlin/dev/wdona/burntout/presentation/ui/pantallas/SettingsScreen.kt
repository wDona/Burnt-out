package dev.wdona.burntout.presentation.ui.pantallas

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.wdona.burntout.presentation.ui.components.ajustes.FilaAjusteInfo
import dev.wdona.burntout.presentation.ui.components.ajustes.FilaAjusteSwitch
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.AjustesViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.AjustesViewModel
import kotlin.text.ifEmpty

class SettingsScreen(val factory: AjustesViewModelFactory) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow // Para poder volver o ir a otra

        val viewmodel = rememberScreenModel { factory.create() }
        SettingsContent(viewmodel, onVolver = { navigator.pop() })
    }
}

@Composable
fun SettingsContent(viewModel: AjustesViewModel, onVolver: () -> Unit) {
    val ajustes by viewModel.ajustesUiState.collectAsStateWithLifecycle()

    ScaffoldBase(
        titulo = "Ajustes",
        onVolver = onVolver,
    ){
        Column {
            FilaAjusteSwitch(
                if (ajustes.primerCuestionarioHecho) "Es primera ejecucion" else "No es primera ejecucion",
                ajustes.primerCuestionarioHecho,
                onSwitch = {
                    viewModel.togglePrimeraEjecucion()
                }
            )
            FilaAjusteInfo("Id de usuario: " + ajustes.idUsuario)
            FilaAjusteInfo("Token de usuario: " + ajustes.token.ifEmpty { "No hay token" })
            FilaAjusteInfo("Nombre de usuario: " + ajustes.nombreUsuario.ifEmpty { "Offline" })
            FilaAjusteInfo("Organizacion de usuario: " + ajustes.idOrganizacion)
            FilaAjusteInfo("Equipo de usuario: " + ajustes.idEquipo)
            FilaAjusteInfo("Version de app: " + ajustes.versionApp)

            TextButton(
                onClick = { viewModel.resetSettings() }
            ) {
                Text("Resetear Datos de Aplicación (Debug)")
            }
        }
    }

}