package dev.wdona.burntout.data.datasource.common

import dev.wdona.burntout.shared.domain.Tarea

interface TareaDataSource {
    suspend fun getTareasByTablero(idTablero: String): List<Tarea>
    suspend fun getTareaById(idTarea: String, idTablero: String): Tarea?
    suspend fun crearTarea(tarea: Tarea) : String
    suspend fun actualizarTarea(tarea: Tarea) : Boolean
    suspend fun eliminarTarea(tareaId: String) : Boolean
    suspend fun insertOrUpdateTarea(tarea: Tarea): Boolean
}
