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

data class FormularioUiState(
    val isLoading: Boolean = true,
    val preguntas: List<Pregunta> = emptyList(),
    val preguntaActual: Pregunta? = null,
    val respuestas: List<Respuesta> = emptyList(),
    val error: String? = null
)

class FormularioViewModel(
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

    fun cargarPreguntas(idUser: Long) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val respuestas = repository.getLastRespuestasByIdUsuario(idUser)

                val preguntas = repository.getPreguntasByOrg(SettingsManager.getIdOrganizacionActual()).sortedBy { pregunta ->
                    respuestas.indexOfFirst { it.idPregunta == pregunta.idPregunta }
                }

                _uiState.update { it.copy(preguntas = preguntas, isLoading = false) }
                seleccionarSiguientePreguntaSinResponder()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun seleccionarSiguientePreguntaSinResponder() {
        val currentState = _uiState.value
        val preguntasList = currentState.preguntas
        val respuestasList = currentState.respuestas
        
        val idsRespondidas = respuestasList.map { it.idPregunta }.toSet()

        val siguiente = preguntasList.firstOrNull { it.idPregunta !in idsRespondidas }
        
        _uiState.update { it.copy(preguntaActual = siguiente) }
    }

    fun cargarRespuesta(idPregunta: Long) {
        screenModelScope.launch {
             // Maybe loading here?
             val respuestas = repository.getRespuestasByPregunta(idPregunta)
             _uiState.update { it.copy(respuestas = it.respuestas + respuestas) }
        }
    }

    fun cargarRespuestasByIdUsuario(idUsuario: Long) {
        screenModelScope.launch {
            val respuestas = repository.getRespuestasByIdUsuario(idUsuario)
             _uiState.update { it.copy(respuestas = respuestas) }
             seleccionarSiguientePreguntaSinResponder()
        }
    }

    fun cargarRespuestasByIdUsuario(idUsuario: Long, date: Long) {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val respuestas = repository.getRespuestasByIdUsuarioAndDate(idUsuario, date)
                _uiState.update { it.copy(respuestas = respuestas, isLoading = false) }
                 seleccionarSiguientePreguntaSinResponder()
            } catch (e: Exception) {
                 _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun crearPregunta(pregunta: Pregunta) {
        screenModelScope.launch {
            repository.crearPregunta(pregunta)
            cargarPreguntas(pregunta.idOrganizacion)
        }
    }

    fun actualizarPregunta(pregunta: Pregunta) {
        screenModelScope.launch {
            repository.actualizarPregunta(pregunta)
            cargarPreguntas(pregunta.idOrganizacion)
        }
    }

    fun eliminarPregunta(idPregunta: Long, idOrg: Long) {
        screenModelScope.launch {
            repository.eliminarPregunta(idPregunta)
            cargarPreguntas(idOrg)
        }
    }

    fun responderPregunta(respuesta: Respuesta) {
        screenModelScope.launch {
            repository.responderPregunta(respuesta)

            val currentState = _uiState.value
            val nuevasRespuestas = currentState.respuestas.filterNot { it.idPregunta == respuesta.idPregunta } + respuesta
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

    fun limpiarRespuestas() {
        screenModelScope.launch {
            _uiState.update { it.copy(respuestas = emptyList()) }
            seleccionarSiguientePreguntaSinResponder()
        }
    }
}
