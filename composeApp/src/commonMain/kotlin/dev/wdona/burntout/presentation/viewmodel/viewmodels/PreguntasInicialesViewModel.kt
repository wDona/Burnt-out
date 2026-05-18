package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.wdona.burntout.domain.repository.PreguntaRespuestaRepository
import dev.wdona.burntout.domain.repository.UsuarioRepository
import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.domain.model.Respuesta
import dev.wdona.burntout.domain.usecase.CalcularRiesgoBurnout
import dev.wdona.burntout.shared.utils.SettingsManager
import dev.wdona.burntout.shared.utils.convertTimestampToStringDate
import dev.wdona.burntout.shared.utils.getCurrentDateString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

data class PreguntasInicialesUiState(
    val isLoading: Boolean = true,
    val preguntas: List<Pregunta> = emptyList(),
    val preguntaActual: Pregunta? = null,
    val respuestas: List<Respuesta> = emptyList(),
    val error: String? = null
)

class PreguntasInicialesViewModel(
    private val repository: PreguntaRespuestaRepository,
    private val usuarioRepository: UsuarioRepository,
    private val calcularRiesgoBurnout: CalcularRiesgoBurnout
) : ScreenModel {

    private val _uiState = MutableStateFlow(FormularioUiState(isLoading = true))
    val uiState: StateFlow<FormularioUiState> = _uiState.asStateFlow()

    val respuestaActual: StateFlow<Respuesta?> = _uiState.asStateFlow().combine(_uiState.asStateFlow()) { state, _ ->
        val pregunta = state.preguntaActual
        val respuestas = state.respuestas
        if (pregunta == null) null
        else respuestas.find { it.idPregunta == pregunta.idPregunta }
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun cargarUltimasNPreguntasPorResponderNoRespondidasHoy(idUser: Long, nPreguntas: Int) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val respuestas = repository.getLastRespuestasByIdUsuario(idUser)
                val hoyString = getCurrentDateString()

                val preguntas = repository.getPreguntasByOrg(SettingsManager.getIdOrganizacionActual()).filter { pregunta ->
                    val respuesta = respuestas.find { it.idPregunta == pregunta.idPregunta }
                    if (respuesta == null) {
                         true
                    } else {
                        hoyString != convertTimestampToStringDate(respuesta.fecha ?: 0L)
                    }
                }.sortedBy { pregunta ->
                    respuestas.indexOfFirst { it.idPregunta == pregunta.idPregunta }
                }.take(nPreguntas)

                val idsRespondidasHoy = respuestas.filter {
                    val fechaRespuesta = convertTimestampToStringDate(it.fecha ?: 0L)
                    fechaRespuesta == hoyString
                }.map { it.idPregunta }.toSet()

                val siguiente = preguntas.firstOrNull { it.idPregunta !in idsRespondidasHoy }

                _uiState.update { it.copy(preguntas = preguntas, isLoading = false, preguntaActual = siguiente) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun seleccionarSiguientePreguntaSinResponder() {
        val currentState = _uiState.value
        val preguntasList = currentState.preguntas
        val respuestasList = currentState.respuestas

        val hoy = getCurrentDateString()
        val idsRespondidasHoy = respuestasList.filter { 
            val fechaRespuesta = convertTimestampToStringDate(it.fecha ?: 0L)
            fechaRespuesta == hoy
        }.map { it.idPregunta }.toSet()

        val siguiente = preguntasList.firstOrNull { it.idPregunta !in idsRespondidasHoy }
        println("ViewModel: Seleccionando siguiente. Respondidas hoy ($hoy): $idsRespondidasHoy. Siguiente: ${siguiente?.idPregunta}")

        _uiState.update { it.copy(preguntaActual = siguiente) }
    }

    fun responderPregunta(respuesta: Respuesta) {
        println("ViewModel: Recibida peticion de responder pregunta ${respuesta.idPregunta}, valor ${respuesta.respuesta}")
        screenModelScope.launch {
            repository.responderPregunta(respuesta)

            val currentState = _uiState.value
            val nuevasRespuestas = currentState.respuestas.filterNot { it.idPregunta == respuesta.idPregunta } + respuesta
            println("ViewModel: Respuestas actualizadas: ${nuevasRespuestas.size}")
            _uiState.update { it.copy(respuestas = nuevasRespuestas) }

            try {
                val resultado = calcularRiesgoBurnout(nuevasRespuestas, currentState.preguntas)
                usuarioRepository.updateRiesgoBurnout(respuesta.idUsuario, resultado.riesgoTotal)
            } catch (e: Exception) {
                println("Error calculando riesgo: ${e.message}")
            }

            seleccionarSiguientePreguntaSinResponder()
        }
    }

    fun preguntaActualFueRespondidaHoy(): Boolean {
        val respuesta = uiState.value.respuestas.find { it.idPregunta == uiState.value.preguntaActual?.idPregunta } ?: return false
        return getCurrentDateString() == convertTimestampToStringDate(respuesta.fecha ?: 0L)
    }
}
