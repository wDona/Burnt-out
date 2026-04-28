package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.wdona.burntout.data.dao.SubtareaRepository
import dev.wdona.burntout.data.dao.TareaRepository
import dev.wdona.burntout.domain.repository.UsuarioRepository
import dev.wdona.burntout.notification.NotificacionProgramador
import dev.wdona.burntout.shared.domain.Subtarea
import dev.wdona.burntout.shared.domain.Tarea
import dev.wdona.burntout.shared.domain.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TareasViewModel(
    private val repository: TareaRepository,
    private val usuarioRepository: UsuarioRepository,
    private val subtareaRepository: SubtareaRepository,
    private val notificacionProgramador: NotificacionProgramador
) : ScreenModel {
    private val _uiState = MutableStateFlow<Tarea?>(null)
    val uiState: StateFlow<Tarea?> = _uiState.asStateFlow()

    private val _listaTareas = MutableStateFlow<List<Tarea>>(emptyList())
    val listaTareas: StateFlow<List<Tarea>> = _listaTareas

    private val _miembros = MutableStateFlow<List<Usuario>>(emptyList())
    val miembros: StateFlow<List<Usuario>> = _miembros.asStateFlow()

    private val _subtareas = MutableStateFlow<List<Subtarea>>(emptyList())
    val subtareas: StateFlow<List<Subtarea>> = _subtareas.asStateFlow()

    fun cargarTareas(tableroId: Long) {
        screenModelScope.launch {
            _listaTareas.value = repository.getTareasByTableroId(tableroId)
        }
    }

    fun cargarTareaPorId(idTarea: Long, idTablero: Long) {
        screenModelScope.launch {
            _uiState.value = repository.getTareaById(idTarea, idTablero)
        }
    }

    fun crearTarea(tarea: Tarea) {
        screenModelScope.launch {
            repository.crearTarea(tarea)
            tarea.fechaVencimiento?.let {
                notificacionProgramador.programarNotificaciones(tarea.idTarea, tarea.titulo, it)
            }
            cargarTareas(tarea.idTableroPerteneciente)
        }
    }

    fun actualizarTarea(tarea: Tarea) {
        screenModelScope.launch {
            repository.actualizarTarea(tarea)
            notificacionProgramador.cancelarNotificaciones(tarea.idTarea)
            tarea.fechaVencimiento?.let {
                notificacionProgramador.programarNotificaciones(tarea.idTarea, tarea.titulo, it)
            }
            cargarTareas(tarea.idTableroPerteneciente)
        }
    }

    fun eliminarTarea(idTarea: Long, tableroId: Long) {
        screenModelScope.launch {
            repository.eliminarTarea(idTarea)
            notificacionProgramador.cancelarNotificaciones(idTarea)
            cargarTareas(tableroId)
        }
    }

    fun cargarMiembrosEquipo(idEquipo: Long) {
        screenModelScope.launch {
            try {
                val usuarios = usuarioRepository.getUsuariosByEquipo(idEquipo)
                _miembros.value = usuarios.sortedBy { it.riesgoBurnout ?: -1.0 }
            } catch (e: Exception) {
                println("Error cargando miembros para tarea: ${e.message}")
            }
        }
    }

    fun cargarSubtareas(idTarea: Long) {
        screenModelScope.launch {
            _subtareas.value = subtareaRepository.getSubtareasByTarea(idTarea)
        }
    }

    fun crearSubtarea(subtarea: Subtarea) {
        screenModelScope.launch {
            subtareaRepository.crearSubtarea(subtarea)
            cargarSubtareas(subtarea.idTareaPerteneciente)
        }
    }

    fun toggleSubtarea(subtarea: Subtarea) {
        screenModelScope.launch {
            subtareaRepository.actualizarSubtarea(subtarea.copy(completado = !subtarea.completado))
            cargarSubtareas(subtarea.idTareaPerteneciente)
        }
    }

    fun eliminarSubtarea(idSubtarea: Long, idTarea: Long) {
        screenModelScope.launch {
            subtareaRepository.eliminarSubtarea(idSubtarea)
            cargarSubtareas(idTarea)
        }
    }
}
