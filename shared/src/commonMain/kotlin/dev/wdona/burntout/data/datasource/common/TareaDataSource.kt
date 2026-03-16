package dev.wdona.burntout.data.datasource.common

import dev.wdona.burntout.shared.domain.Tarea

interface TareaDataSource {
    suspend fun getTareasByTablero(idTablero: Long): List<Tarea>
    suspend fun getTareaById(idTarea: Long, idTablero: Long): Tarea
    suspend fun crearTarea(tarea: Tarea) : Long
    suspend fun actualizarTarea(tarea: Tarea) : Boolean
    suspend fun eliminarTarea(tareaId: Long) : Boolean
}
