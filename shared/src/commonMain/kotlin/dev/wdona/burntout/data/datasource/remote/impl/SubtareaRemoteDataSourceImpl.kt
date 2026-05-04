package dev.wdona.burntout.data.datasource.remote.impl

import dev.wdona.burntout.data.api.SubtareaApi
import dev.wdona.burntout.data.datasource.remote.SubtareaRemoteDataSource
import dev.wdona.burntout.shared.domain.Subtarea
import io.ktor.http.isSuccess

class SubtareaRemoteDataSourceImpl(private val api: SubtareaApi) : SubtareaRemoteDataSource {

    override suspend fun getSubtareasByTarea(idTarea: String): List<Subtarea> =
        api.getSubtareasByTarea(idTarea)

    override suspend fun crearSubtarea(subtarea: Subtarea): String {
        val response = api.crearSubtarea(subtarea)
        return if (response.status.isSuccess()) subtarea.idSubtarea else ""
    }

    override suspend fun actualizarSubtarea(subtarea: Subtarea): Boolean =
        api.actualizarSubtarea(subtarea).status.isSuccess()

    override suspend fun eliminarSubtarea(idSubtarea: String): Boolean =
        api.eliminarSubtarea(idSubtarea).status.isSuccess()
}