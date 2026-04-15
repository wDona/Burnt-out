package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.TareaApi
import dev.wdona.burntout.shared.domain.Tarea
import dev.wdona.burntout.shared.network.ApiClient
import dev.wdona.burntout.shared.utils.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.statement.HttpResponse

class TareaApiImpl(private val client: HttpClient = ApiClient.client) : TareaApi {
    private val TAG = "TareaApiImpl"

    override suspend fun getTareasByTablero(idTablero: Long): List<Tarea> {
        Logger.d(TAG, "getTareasByTablero: $idTablero")
        return client.get("tareas?idTablero=$idTablero").body()
    }

    override suspend fun getTareaById(idTarea: Long, idTablero: Long): Tarea {
        Logger.d(TAG, "getTareaById: $idTarea")
        return client.get("tareas/$idTarea").body()
    }

    override suspend fun crearTarea(tarea: Tarea): HttpResponse {
        Logger.d(TAG, "crearTarea: ${tarea.titulo}")
        return client.post("tareas") {
            contentType(ContentType.Application.Json)
            setBody(tarea)
        }
    }

    override suspend fun actualizarTarea(tarea: Tarea): HttpResponse {
        Logger.d(TAG, "actualizarTarea: ${tarea.idTarea}")
        return client.put("tareas/${tarea.idTarea}") {
            contentType(ContentType.Application.Json)
            setBody(tarea)
        }
    }

    override suspend fun eliminarTarea(idTarea: Long): HttpResponse {
        Logger.d(TAG, "eliminarTarea: $idTarea")
        return client.delete("tareas/$idTarea")
    }
}
