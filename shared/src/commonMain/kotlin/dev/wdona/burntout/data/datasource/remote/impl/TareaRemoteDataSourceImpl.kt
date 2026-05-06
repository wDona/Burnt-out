package dev.wdona.burntout.data.datasource.remote.impl

import dev.wdona.burntout.data.api.TareaApi
import dev.wdona.burntout.data.datasource.remote.TareaRemoteDataSource
import dev.wdona.burntout.shared.domain.Tarea

class TareaRemoteDataSourceImpl(private val api: TareaApi) : TareaRemoteDataSource {
    override suspend fun getTareasByTablero(idTablero: String): List<Tarea> =
        api.getTareasByTablero(idTablero)

    override suspend fun getTareaById(
        idTarea: String,
        idTablero: String
    ): Tarea = api.getTareaById(idTarea, idTablero)

    override suspend fun insertOrUpdateTarea(tarea: Tarea): Boolean {
        return false //FIXME todo
    }

    override suspend fun crearTarea(tarea: Tarea): String {
        val response = api.crearTarea(tarea)

        return if (response.status.value in 200..299) tarea.idTarea else ""
        // FIXME al crear la tarea no se sabe el id
    }

    override suspend fun actualizarTarea(tarea: Tarea): Boolean {
        val response = api.actualizarTarea(tarea)
        return response.status.value in 200..299
    }

    override suspend fun eliminarTarea(tareaId: String): Boolean {
        val status = api.eliminarTarea(tareaId).status.value
        return status in 200..299 || status == 404
    }
}
