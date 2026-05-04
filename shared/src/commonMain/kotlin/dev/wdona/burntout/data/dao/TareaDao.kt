package dev.wdona.burntout.data.dao

import dev.wdona.burntout.shared.domain.Tarea

interface TareaDao {
    suspend fun getTareaById(idTarea: String): Tarea?

    suspend fun getTareasByTablero(idTablero: String): List<Tarea>
    suspend fun crearTarea(tarea: Tarea): String
    suspend fun actualizarTarea(tarea: Tarea): Boolean
    suspend fun eliminarTarea(tareaId: String): Boolean
    suspend fun insertOrUpdateTarea(tarea: Tarea): Boolean
    suspend fun eliminarTareasByTableroId(tableroId: String)
}
