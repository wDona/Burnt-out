package dev.wdona.burntout.data.repository

import dev.wdona.burntout.data.datasource.local.OperacionPendienteLocalDataSource
import dev.wdona.burntout.domain.model.OperacionPendiente
import dev.wdona.burntout.domain.repository.OperacionesPendientesRepository


class OperacionesPendientesRepositoryImpl(
    val operacionPendienteLocalDataSource: OperacionPendienteLocalDataSource
) : OperacionesPendientesRepository {
    override suspend fun getOperacionesPendientes(): List<OperacionPendiente> {
        return operacionPendienteLocalDataSource.getOperacionesPendientes()
    }

    override suspend fun insertOperacionPendiente(
        tipoAccion: String,
        tablaAfectada: String,
        idAfectado: String,
        datosJson: String,
        timestampCreacion: Long
    ) {
        operacionPendienteLocalDataSource.insertOperacionPendiente(
            tipoAccion = tipoAccion,
            tablaAfectada = tablaAfectada,
            idAfectado = idAfectado,
            datosJson = datosJson,
            timestampCreacion = timestampCreacion
        )
    }

    override suspend fun deleteOperacionPendientePorEstado(estado: Long) {
        operacionPendienteLocalDataSource.deleteOperacionPendientePorEstado(estado)
    }

    override suspend fun cambiarEstadoOperacion(
        sincronizado: Long,
        idOperacion: Long
    ) {
        operacionPendienteLocalDataSource.cambiarEstadoOperacion(
            sincronizado = sincronizado,
            idOperacion = idOperacion
        )
    }

    override suspend fun deleteOperacionPendiente(idOperacion: Long) {
        operacionPendienteLocalDataSource.deleteOperacionPendiente(idOperacion)
    }
}