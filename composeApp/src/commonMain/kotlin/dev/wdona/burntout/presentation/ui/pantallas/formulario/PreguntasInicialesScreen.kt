package dev.wdona.burntout.presentation.ui.pantallas.formulario

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.FormularioViewModelFactory
import dev.wdona.burntout.domain.model.Respuesta
import dev.wdona.burntout.presentation.ui.components.common.FilaTextoPlaceholder
import dev.wdona.burntout.presentation.ui.pantallas.MainScreen
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.AjustesViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.LeaderboardViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.EquipoViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.MiPerfilViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.OperacionesPendientesViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.TablerosViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.TareasViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.PreguntasInicialesViewModel
import dev.wdona.burntout.shared.utils.SettingsManager
import kotlin.math.round

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
    private val idEquipo: Long = SettingsManager.getIdEquipoActual()
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
                //viewModel.cargarUltimasNPreguntasPorResponderNoRespondidasHoy(idOrganizacion, nPreguntas)
                val idUsuario = SettingsManager.getIdUsuarioActual()
                // Usar idUsuario en lugar de idOrganizacion para cargar respuestas del usuario
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
        println("CLICK en Responder: selected=$selectedCantidad, pregunta=$preguntaActual por usuario ${SettingsManager.getIdUsuarioActual()}")
        if (selectedCantidad != null && preguntaActual != null) {
            val respuesta = Respuesta(
                idRespuesta = java.util.UUID.randomUUID().toString(),
                idUsuario = SettingsManager.getIdUsuarioActual(),
                idPregunta = preguntaActual.idPregunta,
                anonimo = SettingsManager.isRespuestasAnonimas(),
                respuesta = selectedCantidad!!.toLong(),
                fecha = System.currentTimeMillis() / 1000L
            )
            println("Enviando respuesta con fecha ${respuesta.fecha} (${dev.wdona.burntout.shared.utils.convertTimestampToStringDate(respuesta.fecha ?: 0L)})")

            viewModel.responderPregunta(respuesta)

            //viewModel.seleccionarSiguientePreguntaSinResponder() // Eliminado porque el VM ya lo hace
        } else {
             println("NO SE PUEDE RESPONDER: selected o pregunta es null")
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



    val tiempo = if (((uiState.preguntas.size - nRespuestas) * tiempoPorPregunta) > 60f) {
        "~${round(((uiState.preguntas.size - nRespuestas) * tiempoPorPregunta / 60f) * 100.0) / 100f} mins"
    } else {
        "~${round((uiState.preguntas.size - nRespuestas) * tiempoPorPregunta * 100.0) / 100f} segs"
    }

    val titulo = "Diario inicial"
    val subtitulo = if (isLoading) "" else tiempo

    ScaffoldBase(
        titulo = titulo,
        subtitle = subtitulo,
        topBarWindowInsets = WindowInsets.safeDrawing,

        onVolver = onVolver,
        onSaltar = onSaltar,
        onFAB = if (preguntaActual != null) responderAccion else null,
        fabEnabled = selectedCantidad != null && preguntaActual != null,
        iconFAB = {
            Icon(
                Icons.AutoMirrored.Filled.NavigateNext,
                "Siguiente pregunta"
            )
        },
        textoFAB = "", // TODO: PONER TEXTO DE TERMINAR EN LA ULTIMA
        fabModifier = Modifier
            .focusRequester(focusRequester)
            .onPreviewKeyEvent {
                if (it.type == KeyEventType.KeyUp && (it.key == Key.Enter || it.key == Key.NumPadEnter)) {
                    responderAccion()
                    true
                } else {
                    false
                }
            },
    ) {
        androidx.compose.animation.Crossfade(targetState = isLoading) { loading ->
            if (loading) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    FilaTextoPlaceholder(
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                    )

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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        AnimatedContent(
                            targetState = preguntaActual,
                            transitionSpec = {
                                (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                    slideOutHorizontally { width -> -width } + fadeOut())
                            },
                            contentKey = { it?.idPregunta ?: "final" },
                            label = "PreguntaAnimation"
                        ) { targetPregunta ->
                            val scrollState = rememberScrollState()
                            val showMoreIcon by remember {
                                derivedStateOf {
                                    scrollState.maxValue > 0 && scrollState.value < scrollState.maxValue
                                }
                            }
    
                            Box(Modifier.fillMaxSize()) {
                                Column(
                                    Modifier
                                        .verticalScroll(scrollState)
                                        .padding(bottom = 16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (targetPregunta != null) {
                                        println("Carga preguntasInicialesContent la pregunta: ${targetPregunta.idPregunta}")
                                        
                                        Text(
                                            text = targetPregunta.pregunta,
                                            style = MaterialTheme.typography.titleLarge,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 24.dp, start = 8.dp, end = 8.dp),
                                            textAlign = TextAlign.Center
                                        )
                                        
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
                                                            selected = (selectedCantidad == cantidadOpcion && preguntaActual?.idPregunta == targetPregunta.idPregunta),
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
                                                        selected = (selectedCantidad == cantidadOpcion && preguntaActual?.idPregunta == targetPregunta.idPregunta),
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
                                    } else {
                                        println("Carga el else de preguntasInicialesContent")
                                        LaunchedEffect(uiState) {
                                            onSaltar?.invoke()
                                        }
                                    }
                                }

                                ScrollMoreIndicator(
                                    visible = showMoreIcon,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 8.dp)
                                )
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