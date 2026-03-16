package dev.wdona.burntout.presentation.ui.pantallas.tablero

import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.TablerosViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.TablerosViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.ui.pantallas.SettingsContent
import dev.wdona.burntout.shared.domain.Tablero
import dev.wdona.burntout.shared.utils.SettingsManager


class MenuCrearTableroScreen(val factory: TablerosViewModelFactory) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow // Para poder volver o ir a otra
        val viewModel: TablerosViewModel = rememberScreenModel { factory.create() }

        MenuCrearTableroContent(
            tablerosViewModel = viewModel,
            onVolver = { navigator.pop() }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuCrearTableroContent(tablerosViewModel: TablerosViewModel, onVolver: () -> Unit) {
    var textStateNombreTablero by remember { mutableStateOf("") }

    val ejecutarEnvio: () -> Unit = {
        if (textStateNombreTablero.isNotBlank()) {
            tablerosViewModel.crearTablero(
                Tablero(
                    0L,
                    textStateNombreTablero,
                    SettingsManager.getIdOrganizacionActual(), // FIXME
                    SettingsManager.getIdEquipoActual() // FIXME
                )
            )

            textStateNombreTablero = ""
            onVolver()

        }

    }
    ScaffoldBase(
        titulo = "Crear Tablero",
        onVolver = onVolver,
        onFAB = ejecutarEnvio,
        textoFAB = "Crear"
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(
                value = textStateNombreTablero,
                onValueChange = { newText ->
                    textStateNombreTablero = newText },
                label = { Text("Titulo") },
                placeholder = { Text("Ej. Lista de la compra") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { ejecutarEnvio() }
                )
            )
        }
    }
}