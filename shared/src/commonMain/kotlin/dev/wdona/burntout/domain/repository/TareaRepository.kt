package dev.wdona.burntout.data.dao

import dev.wdona.burntout.shared.domain.Tarea

interface TareaRepository {
    suspend fun getTareasByTableroId(tableroId: String): List<Tarea>
    suspend fun getTareaById(idTarea: String, idTablero: String): Tarea?
    suspend fun crearTarea(tarea: Tarea)
    suspend fun actualizarTarea(tarea: Tarea)
    suspend fun eliminarTarea(idTarea: String)
}