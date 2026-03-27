package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.TareaApi
import dev.wdona.burntout.shared.domain.Tarea
import dev.wdona.burntout.shared.network.ApiClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse

class TareaApiImpl(private val client: HttpClient = ApiClient.client) : TareaApi {
    override suspend fun getTareasByTablero(idTablero: Long): List<Tarea> =
        client.get("tareas?idTablero=$idTablero").body()

    override suspend fun getTareaById(idTarea: Long, idTablero: Long): Tarea =
        client.get("tareas/$idTarea").body()

    override suspend fun crearTarea(tarea: Tarea): HttpResponse =
        client.post("tareas") { setBody(tarea) }

    override suspend fun actualizarTarea(tarea: Tarea): HttpResponse =
        client.put("tareas/${tarea.idTarea}") { setBody(tarea) }

    override suspend fun eliminarTarea(idTarea: Long): HttpResponse =
        client.delete("tareas/$idTarea")
}