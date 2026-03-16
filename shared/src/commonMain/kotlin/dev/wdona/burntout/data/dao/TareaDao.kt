package dev.wdona.burntout.data.dao

import dev.wdona.burntout.shared.domain.Tarea

interface TareaDao {
    suspend fun getTareaById(idTarea: Long): Tarea
    suspend fun getTareasByTablero(idTablero: Long): List<Tarea>
    suspend fun crearTarea(tarea: Tarea): Long
    suspend fun actualizarTarea(tarea: Tarea): Boolean
    suspend fun eliminarTarea(tareaId: Long): Boolean
    suspend fun insertOrUpdateTarea(tarea: Tarea): Boolean
    suspend fun eliminarTareasByTableroId(tableroId: Long)
}
