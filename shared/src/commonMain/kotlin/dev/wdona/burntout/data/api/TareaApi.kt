package dev.wdona.burntout.data.api

import dev.wdona.burntout.shared.domain.Tarea
import io.ktor.client.statement.HttpResponse

interface TareaApi {
    suspend fun getTareasByTablero(idTablero: String): List<Tarea>
    suspend fun getTareaById(idTarea: String, idTablero: String): Tarea
    suspend fun crearTarea(tarea: Tarea): HttpResponse
    suspend fun actualizarTarea(tarea: Tarea) : HttpResponse
    suspend fun eliminarTarea(idTarea: String) : HttpResponse
}