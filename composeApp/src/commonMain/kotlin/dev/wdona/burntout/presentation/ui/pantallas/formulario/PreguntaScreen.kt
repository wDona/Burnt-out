package dev.wdona.burntout.presentation.ui.pantallas.formulario

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.FormularioViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.FormularioViewModel
import dev.wdona.burntout.domain.model.Respuesta
import dev.wdona.burntout.presentation.ui.components.common.FilaTextoPlaceholder
import dev.wdona.burntout.shared.utils.SettingsManager

class PreguntaScreen(private val viewModelFactory: FormularioViewModelFactory, private val onVolver: (() -> Unit)? = null) : Screen {

    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { viewModelFactory.createFormularioViewModel() }

        val idOrganizacion = SettingsManager.getIdOrganizacionActual()
        
        LaunchedEffect(idOrganizacion) {
            try {
                viewModel.cargarPreguntas(idOrganizacion)
                viewModel.cargarRespuestasByIdUsuario(
                    SettingsManager.getIdUsuarioActual(),
                    System.currentTimeMillis() / 1000L
                )
            } catch (e: Exception) {
                println("Error al cargar preguntas: ${e.message}")
            }
        }

        PreguntaContent(
            onVolver = onVolver,
            viewModel = viewModel
        )
    }
}

@Composable
fun PreguntaContent(onVolver: (() -> Unit)? = null, viewModel: FormularioViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listaPreguntas = uiState.preguntas
    val preguntaActual = uiState.preguntaActual
    val isLoading = uiState.isLoading
    val respuestaActual by viewModel.respuestaActual.collectAsStateWithLifecycle()

    var selectedCantidad by remember { mutableStateOf<Int?>(null) }
    val focusRequester = remember { FocusRequester() }

    val responderAccion = {
        if (selectedCantidad != null && preguntaActual != null) {
            val respuesta = Respuesta(
                idUsuario = SettingsManager.getIdUsuarioActual(),
                idPregunta = preguntaActual.idPregunta,
                anonimo = false,
                respuesta = selectedCantidad!!.toLong(),
                fecha = System.currentTimeMillis() / 1000L
            )

            viewModel.responderPregunta(respuesta)

            viewModel.seleccionarSiguientePreguntaSinResponder()
        }
    }

    LaunchedEffect(preguntaActual) {
        selectedCantidad = null
    }

    LaunchedEffect(respuestaActual) {
        respuestaActual?.let {
            selectedCantidad = it.respuesta.toInt()
        }
    }

    LaunchedEffect(selectedCantidad) {
        if (selectedCantidad != null) {
            focusRequester.requestFocus()
        }
    }
    
    val titulo = if (isLoading) "" else "Diario"

    ScaffoldBase (
        titulo = titulo,
        onVolver = onVolver,
        onFAB = if (preguntaActual != null) responderAccion else null,
        fabEnabled = selectedCantidad != null && preguntaActual != null,
        iconFAB = {
            Icon(
                Icons.AutoMirrored.Filled.NavigateNext,
                "Siguiente pregunta"
            )
        },
        textoFAB = "Siguiente pregunta",
        fabModifier = Modifier
            .focusRequester(focusRequester)
            .onPreviewKeyEvent {
                if (it.type == KeyEventType.KeyUp && (it.key == Key.Enter || it.key == Key.NumPadEnter)) {
                    responderAccion()
                    true
                } else {
                    false
                }
            }
    ) {
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                (0..1).forEach { _ ->
                    FilaTextoPlaceholder(
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column {
                            (0..6).forEach { cantidadOpcion ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = false,
                                        onClick = null
                                    )
                                    FilaTextoPlaceholder(paddingEnd = 32)
                                }

                            }
                        }
                    }
                }
            }
        } else {
            val scrollState = rememberScrollState()

            val showMoreIcon by remember {
                derivedStateOf {
                    scrollState.maxValue > 0 && scrollState.value < scrollState.maxValue
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (preguntaActual?.pregunta != null) {
                    Text(
                        text = preguntaActual.pregunta,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    if (preguntaActual?.pregunta != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(scrollState),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Column(
                                Modifier
                                    .selectableGroup()
                                    .onPreviewKeyEvent {
                                        if (it.type == KeyEventType.KeyUp && (it.key == Key.Enter || it.key == Key.NumPadEnter)) {
                                            if (selectedCantidad != null) {
                                                responderAccion()
                                                true
                                            } else {
                                                false
                                            }
                                        } else {
                                            false
                                        }
                                    }
                            ) {
                                (0..6).forEach { cantidadOpcion ->
                                    val textoRespuesta = when (cantidadOpcion) {
                                        0 -> "Nunca / Ninguna vez"
                                        1 -> "Casi nunca / Pocas veces al año"
                                        2 -> "Algunas veces / Una vez al mes o menos"
                                        3 -> "Regularmente / Pocas veces al mes"
                                        4 -> "Bastantes veces / Una vez por semana"
                                        5 -> "Casi siempre / Pocas veces por semana"
                                        6 -> "Siempre / Todos los días"
                                        else -> "invalid"
                                    }


                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                            .selectable(
                                                selected = (selectedCantidad == cantidadOpcion),
                                                onClick = {
                                                    selectedCantidad = cantidadOpcion
                                                    focusRequester.requestFocus()
                                                },
                                                role = Role.RadioButton
                                            )
                                            .padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = (selectedCantidad == cantidadOpcion),
                                            onClick = null
                                        )
                                        Text(
                                            text = textoRespuesta,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.padding(start = 16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        ScrollMoreIndicator(
                            visible = showMoreIcon,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 8.dp)
                        )
                    } else {
                        // CENTRAR
                        Column (
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ){
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
                            OutlinedButton(onClick = {
                                viewModel.limpiarRespuestas()
                            }) {
                                Text("Reiniciar preguntas")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScrollMoreIndicator(visible: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Desliza para ver más",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}