package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.TareaApi
import dev.wdona.burntout.shared.domain.Tarea
import dev.wdona.burntout.shared.network.ApiClient
import dev.wdona.burntout.shared.network.ApiClient.client
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse

class TareaApiImpl(private val client: HttpClient = ApiClient.client) : TareaApi {
    override suspend fun getTareasByTablero(idTablero: Long): List<Tarea> = client.get("$idTablero/").body()

    override suspend fun getTareaById(idTarea: Long, idTablero: Long): Tarea = client.get("$idTablero/$idTarea").body()

    override suspend fun crearTarea(tarea: Tarea) : HttpResponse =
        client.post(
            "/nuevo/tarea/tarea=" +
                    "${tarea.titulo}" +
                    "&descripcion=${tarea.descripcion}" +
                    "&estado=${tarea.estado}" +
                    "&idTableroPerteneciente=${tarea.idTableroPerteneciente}" +
                    "&idUsuarioAsignado=${tarea.idUsuarioAsignado}"
        )

    override suspend fun actualizarTarea(tarea: Tarea) : HttpResponse =
        client.post(
            "/actualizar/tarea/idTarea=${tarea.idTarea}" +
                    "&titulo=${tarea.titulo}" +
                    "&descripcion=${tarea.descripcion}" +
                    "&estado=${tarea.estado}"
        )

    override suspend fun eliminarTarea(idTarea: Long): HttpResponse {
        return client.post("/eliminar/tarea/idTarea=$idTarea")
    }
}