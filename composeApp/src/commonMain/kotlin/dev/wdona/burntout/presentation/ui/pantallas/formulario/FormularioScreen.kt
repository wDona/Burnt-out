package dev.wdona.burntout.presentation.ui.pantallas.formulario

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.FormularioViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.FormularioViewModel
import dev.wdona.burntout.shared.utils.SettingsManager

class FormularioScreen(private val viewModelFactory: FormularioViewModelFactory, private val onVolver: (() -> Unit)? = null) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { viewModelFactory.create() }

        val idOrganizacion = SettingsManager.getIdOrganizacionActual()
        
        LaunchedEffect(idOrganizacion) {
            try {
                viewModel.cargarPreguntas(idOrganizacion)
                viewModel.cargarRespuestasByIdUsuario(SettingsManager.getIdUsuarioActual())
            } catch (e: Exception) {
                println("Error al cargar preguntas: ${e.message}")
            }
        }

        FormularioContent(
            onVolver = onVolver,
            onFAB = { navigator.push(
                MenuCrearPreguntaScreen(
                        viewModelFactory,
                        onVolver = { navigator.pop() }
                        )
                    )
                },
            viewModel = viewModel
        )
    }
}

@Composable
fun FormularioContent(onVolver: (() -> Unit)? = null, onFAB: () -> Unit, viewModel: FormularioViewModel) {
    val listaPreguntas by viewModel.preguntas.collectAsStateWithLifecycle()
    val listaRespuestas by viewModel.respuestas.collectAsStateWithLifecycle()

    ScaffoldBase (
        titulo = "Diario",
        onVolver = onVolver,
        onFAB = onFAB,
        textoFAB = "Nueva pregunta"
    ) {

    }
}