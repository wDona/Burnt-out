package dev.wdona.burntout.presentation.ui.pantallas.formulario

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.wdona.burntout.domain.model.Respuesta
import dev.wdona.burntout.presentation.ui.components.formulario.ListaOpcionesRespuesta
import dev.wdona.burntout.presentation.ui.components.formulario.SkeletonPregunta
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.ui.pantallas.MainScreen
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.AjustesViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.EquipoViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.FormularioViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.LeaderboardViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.MiPerfilViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.OperacionesPendientesViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.TablerosViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.TareasViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.PreguntasInicialesViewModel
import dev.wdona.burntout.shared.utils.SettingsManager

class PreguntasInicialesScreen(
    private val viewModelFactory: FormularioViewModelFactory,
    private val onVolver: (() -> Unit)? = null,
    private val nPreguntas: Int,

    private val tareaFactory: TareasViewModelFactory,
    private val equipoFactory: EquipoViewModelFactory,
    private val perfilFactory: MiPerfilViewModelFactory,
    private val tableroFactory: TablerosViewModelFactory,
    private val leaderboardFactory: LeaderboardViewModelFactory,
    private val ajustesFactory: AjustesViewModelFactory,
    private val operacionesPendientesFactory: OperacionesPendientesViewModelFactory,
) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val onTerminar: () -> Unit = {
            navigator.replaceAll(
                MainScreen(
                    tareaFactory = tareaFactory,
                    equipoFactory = equipoFactory,
                    perfilFactory = perfilFactory,
                    tableroFactory = tableroFactory,
                    leaderboardFactory = leaderboardFactory,
                    ajustesFactory = ajustesFactory,
                    formularioFactory = viewModelFactory,
                    operacionesPendientesFactory = operacionesPendientesFactory,
                )
            )
        }
        val viewModel = rememberScreenModel { viewModelFactory.createPreguntasInicialesViewModel() }

        val idOrganizacion = SettingsManager.getIdOrganizacionActual()

        LaunchedEffect(idOrganizacion) {
            try {
                val idUsuario = SettingsManager.getIdUsuarioActual()
                viewModel.cargarUltimasNPreguntasPorResponderNoRespondidasHoy(idUsuario, nPreguntas)
            } catch (e: Exception) {
                println("Error al cargar preguntas: ${e.message}")
            }
        }

        PreguntasInicialesContent(
            onVolver = onVolver,
            viewModel = viewModel,
            onSaltar = onTerminar
        )
    }
}

@Composable
fun PreguntasInicialesContent(onVolver: (() -> Unit)? = null, viewModel: PreguntasInicialesViewModel, onSaltar: (() -> Unit)? = null) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val preguntaActual = uiState.preguntaActual
    val isLoading = uiState.isLoading
    val nRespuestas = uiState.respuestas.size
    val respuestaActual by viewModel.respuestaActual.collectAsStateWithLifecycle()
    val tiempoPorPregunta = 8

    LaunchedEffect(isLoading, preguntaActual) {
        if (!isLoading && preguntaActual == null && uiState.preguntas.isNotEmpty()) {
            SettingsManager.setPrimerCuestionarioHecho(true)
            SettingsManager.setUltimaFechaCuestionarioHoy()
        }
    }

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
        }
    }

    LaunchedEffect(preguntaActual) { selectedCantidad = null }
    LaunchedEffect(respuestaActual) { respuestaActual?.let { selectedCantidad = it.respuesta.toInt() } }
    LaunchedEffect(selectedCantidad) { if (selectedCantidad != null) focusRequester.requestFocus() }

    val totalSegundos = (uiState.preguntas.size - nRespuestas) * tiempoPorPregunta
    val tiempo = if (totalSegundos >= 60) {
        val minutos = totalSegundos / 60
        val segundos = totalSegundos % 60
        "~$minutos:${if (segundos < 10) "0$segundos" else "$segundos"} mins"
    } else {
        "~$totalSegundos segs"
    }

    ScaffoldBase(
        titulo = "Diario inicial",
        subtitle = if (isLoading) "" else tiempo,
        topBarWindowInsets = WindowInsets.safeDrawing,
        onVolver = onVolver,
        onSaltar = onSaltar,
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
            },
    ) {
        Crossfade(targetState = isLoading) { loading ->
            if (loading) {
                SkeletonPregunta(nLineasTitulo = 1)
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
                                            .padding(bottom = 24.dp, start = 8.dp, end = 8.dp),
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
                                LaunchedEffect(uiState) { onSaltar?.invoke() }
                            }
                        }
                    }
                }
            }
        }
    }
}
