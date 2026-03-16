package dev.wdona.burntout.presentation.viewmodel.viewmodels

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.wdona.burntout.data.dao.TareaRepository
import dev.wdona.burntout.shared.domain.Tarea
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TareasViewModel(private val repository: TareaRepository) : ScreenModel {
    private val _uiState = MutableStateFlow<Tarea?>(null)
    val uiState: StateFlow<Tarea?> = _uiState.asStateFlow()

    private val _listaTareas = MutableStateFlow<List<Tarea>>(emptyList())
    val listaTareas: StateFlow<List<Tarea>> = _listaTareas

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
            cargarTareas(tarea.idTableroPerteneciente)
        }
    }

    fun actualizarTarea(tarea: Tarea) {
        screenModelScope.launch {
            repository.actualizarTarea(tarea)
            cargarTareas(tarea.idTableroPerteneciente)
        }
    }

    fun eliminarTarea(idTarea: Long, tableroId: Long) {
        screenModelScope.launch {
            repository.eliminarTarea(idTarea)
            cargarTareas(tableroId)
        }
    }
}
