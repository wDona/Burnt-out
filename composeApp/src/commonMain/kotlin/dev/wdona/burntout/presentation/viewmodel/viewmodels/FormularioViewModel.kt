package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.wdona.burntout.data.dao.PreguntaRespuestaRepository
import dev.wdona.burntout.domain.repository.UsuarioRepository
import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.domain.model.Respuesta
import dev.wdona.burntout.domain.usecase.CalcularRiesgoBurnout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class FormularioViewModel(
    private val repository: PreguntaRespuestaRepository,
    private val usuarioRepository: UsuarioRepository,
    private val calcularRiesgoBurnout: CalcularRiesgoBurnout
) : ScreenModel {
    
    private val _preguntas = MutableStateFlow<List<Pregunta>>(emptyList())
    val preguntas: StateFlow<List<Pregunta>> = _preguntas.asStateFlow()

    private val _preguntaActual = MutableStateFlow<Pregunta?>(null)
    val preguntaActual: StateFlow<Pregunta?> = _preguntaActual.asStateFlow()

    private val _respuestas = MutableStateFlow<List<Respuesta>>(emptyList())
    val respuestas: StateFlow<List<Respuesta>> = _respuestas.asStateFlow()
    
    val respuestaActual: StateFlow<Respuesta?> = combine(
        _respuestas,
        _preguntaActual
    ) { respuestas, pregunta ->
        if (pregunta == null) null
        else respuestas.find { it.idPregunta == pregunta.idPregunta }
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun cargarPreguntas(idOrg: Long) {
        screenModelScope.launch {
            _preguntas.value = repository.getPreguntasByOrg(idOrg)
        }
    }

    fun seleccionarSiguientePreguntaSinResponder() {
        val preguntasList = _preguntas.value
        val respuestasList = _respuestas.value
        
        val idsRespondidas = respuestasList.map { it.idPregunta }.toSet()

        val siguiente = preguntasList.firstOrNull { it.idPregunta !in idsRespondidas }
        
        _preguntaActual.value = siguiente
    }

    fun cargarRespuesta(idPregunta: Long) {
        screenModelScope.launch {
            _respuestas.value = repository.getRespuestasByPregunta(idPregunta)
        }
    }

    fun cargarRespuestasByIdUsuario(idUsuario: Long) {
        screenModelScope.launch {
            _respuestas.value = repository.getRespuestasByIdUsuario(idUsuario)
        }
    }

    fun cargarRespuestasByIdUsuario(idUsuario: Long, date: Long) {
        screenModelScope.launch {
            _respuestas.value = repository.getRespuestasByIdUsuarioAndDate(idUsuario, date)
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

            val nuevasRespuestas = _respuestas.value.filterNot { it.idPregunta == respuesta.idPregunta } + respuesta
            _respuestas.value = nuevasRespuestas

            try {
                val resultado = calcularRiesgoBurnout(nuevasRespuestas)
                usuarioRepository.updateRiesgoBurnout(respuesta.idUsuario, resultado.riesgoTotal)
            } catch (e: Exception) {
                println("Error calculando riesgo: ${e.message}")
            }

            seleccionarSiguientePreguntaSinResponder()
        }
    }

    fun limpiarRespuestas() {
        screenModelScope.launch {
            _respuestas.value = emptyList()
            seleccionarSiguientePreguntaSinResponder()
        }
    }
}
