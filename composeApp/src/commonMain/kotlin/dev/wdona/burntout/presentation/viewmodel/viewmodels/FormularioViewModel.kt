package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.wdona.burntout.data.dao.PreguntaRespuestaRepository
import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.shared.domain.Respuesta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FormularioViewModel(private val repository: PreguntaRespuestaRepository) : ScreenModel {
    
    private val _preguntas = MutableStateFlow<List<Pregunta>>(emptyList())
    val preguntas: StateFlow<List<Pregunta>> = _preguntas.asStateFlow()

    private val _respuestas = MutableStateFlow<List<Respuesta>>(emptyList())
    val respuestas: StateFlow<List<Respuesta>> = _respuestas.asStateFlow()

    fun cargarPreguntas(idOrg: Long) {
        screenModelScope.launch {
            _preguntas.value = repository.getPreguntasByOrg(idOrg)
        }
    }

    fun cargarRespuesta(idPregunta: Long) {
        screenModelScope.launch {
            _respuestas.value = repository.getRespuestasByPregunta(idPregunta)
        }
    }

    fun cargarRespuestasByIdUsuario(idUsuario: Long) {
        screenModelScope.launch {
            _respuestas.value = repository.getRespuestasByUser(idUsuario)
        }
    }

    fun cargarRespuestasByIdUsuario(idUsuario: Long, date: Long) {
        screenModelScope.launch {
            _respuestas.value = repository.getRespuestasByUserAndDate(idUsuario, date)
        }
    }

    fun cargarRespuestas() {
        // TODO
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
            cargarRespuesta(respuesta.idPregunta)
        }
    }
}
