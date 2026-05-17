package dev.wdona.burntout.presentation.ui.pantallas.formulario

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import dev.wdona.burntout.domain.model.Respuesta
import dev.wdona.burntout.presentation.ui.components.formulario.ListaOpcionesRespuesta
import dev.wdona.burntout.presentation.ui.components.formulario.SkeletonPregunta
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.FormularioViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.FormularioUiState
import dev.wdona.burntout.presentation.viewmodel.viewmodels.FormularioViewModel
import dev.wdona.burntout.shared.utils.SettingsManager
import dev.wdona.burntout.shared.utils.getCurrentTimestampSeconds

class PreguntaScreen(private val viewModelFactory: FormularioViewModelFactory, private val onVolver: (() -> Unit)? = null) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { viewModelFactory.createFormularioViewModel() }

        val idOrganizacion = SettingsManager.getIdOrganizacionActual()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val preguntas = uiState.preguntas

        LaunchedEffect(idOrganizacion) {
            try {
                if (preguntas.isEmpty()) {
                    viewModel.cargarPreguntas(idOrganizacion)
                    viewModel.cargarRespuestasByIdUsuario(
                        SettingsManager.getIdUsuarioActual(),
                        getCurrentTimestampSeconds()
                    )
                }
            } catch (e: Exception) {
                println("Error al cargar preguntas: ${e.message}")
            }
        }

        PreguntaContent(
            onVolver = onVolver,
            viewModel = viewModel,
            uiState = uiState
        )
    }
}

@Composable
fun PreguntaContent(onVolver: (() -> Unit)? = null, viewModel: FormularioViewModel, uiState: FormularioUiState = viewModel.uiState.collectAsStateWithLifecycle().value) {
    val preguntaActual = uiState.preguntaActual
    val isLoading = uiState.isLoading
    val respuestaActual by viewModel.respuestaActual.collectAsStateWithLifecycle()

    var selectedCantidad by remember { mutableStateOf<Int?>(null) }
    val focusRequester = remember { FocusRequester() }

    val responderAccion = {
        if (selectedCantidad != null && preguntaActual != null) {
            val respuesta = Respuesta(
                idRespuesta = java.util.UUID.randomUUID().toString(),
                idUsuario = SettingsManager.getIdUsuarioActual(),
                idPregunta = preguntaActual.idPregunta,
                anonimo = SettingsManager.isRespuestasAnonimas(),
                respuesta = selectedCantidad!!.toLong(),
                fecha = System.currentTimeMillis() / 1000L
            )
            viewModel.responderPregunta(respuesta)
            viewModel.seleccionarSiguientePreguntaSinResponder()
        }
    }

    LaunchedEffect(preguntaActual) { selectedCantidad = null }
    LaunchedEffect(respuestaActual) { respuestaActual?.let { selectedCantidad = it.respuesta.toInt() } }
    LaunchedEffect(selectedCantidad) { if (selectedCantidad != null) focusRequester.requestFocus() }

    ScaffoldBase(
        titulo = if (isLoading) "" else "Diario",
        onVolver = onVolver,
        onFAB = if (preguntaActual != null) responderAccion else null,
        fabEnabled = selectedCantidad != null && preguntaActual != null,
        iconFAB = { Icon(Icons.AutoMirrored.Filled.NavigateNext, "Siguiente pregunta") },
        textoFAB = "",
        fabModifier = Modifier
            .focusRequester(focusRequester)
            .onPreviewKeyEvent {
                if (it.type == KeyEventType.KeyUp && (it.key == Key.Enter || it.key == Key.NumPadEnter)) {
                    responderAccion(); true
                } else false
            }
    ) {
        Crossfade(targetState = isLoading) { loading ->
            if (loading) {
                SkeletonPregunta(nLineasTitulo = 2)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        AnimatedContent(
                            targetState = preguntaActual,
                            transitionSpec = {
                                (slideInHorizontally { w -> w } + fadeIn()).togetherWith(
                                    slideOutHorizontally { w -> -w } + fadeOut())
                            },
                            contentKey = { it?.idPregunta ?: "final" },
                            label = "PreguntaAnimation"
                        ) { targetPregunta ->
                            if (targetPregunta != null) {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    Text(
                                        text = targetPregunta.pregunta,
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 24.dp),
                                        textAlign = TextAlign.Center
                                    )
                                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                        ListaOpcionesRespuesta(
                                            selectedCantidad = selectedCantidad,
                                            preguntaIdActual = preguntaActual?.idPregunta,
                                            targetPreguntaId = targetPregunta.idPregunta,
                                            onSelect = { v ->
                                                selectedCantidad = v
                                                focusRequester.requestFocus()
                                            },
                                            onConfirmar = responderAccion
                                        )
                                    }
                                }
                            } else {
                                TodoContestarState(onReiniciar = { viewModel.limpiarRespuestas() })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TodoContestarState(onReiniciar: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Todo contestado",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Text(
            text = "Quieres contestar otra vez?",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )
        OutlinedButton(onClick = onReiniciar) {
            Text("Reiniciar preguntas")
        }
    }
}
