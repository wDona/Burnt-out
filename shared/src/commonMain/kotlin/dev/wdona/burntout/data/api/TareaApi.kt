package dev.wdona.burntout.data.api

import dev.wdona.burntout.shared.domain.Tarea
import io.ktor.client.statement.HttpResponse

interface TareaApi {
    suspend fun getTareasByTablero(idTablero: Long): List<Tarea>
    suspend fun getTareaById(idTarea: Long, idTablero: Long): Tarea
    suspend fun crearTarea(tarea: Tarea): HttpResponse
    suspend fun actualizarTarea(tarea: Tarea) : HttpResponse
    suspend fun eliminarTarea(idTarea: Long) : HttpResponse
}