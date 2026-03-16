package dev.wdona.burntout.data.datasource.local.impl

import dev.wdona.burntout.data.dao.OperacionPendienteDao
import dev.wdona.burntout.data.dao.impl.OperacionPendienteDaoImpl
import dev.wdona.burntout.data.datasource.local.OperacionPendienteLocalDataSource
import dev.wdona.burntout.domain.model.OperacionPendiente

class OperacionPendienteLocalDataSourceImpl(val dao: OperacionPendienteDao) : OperacionPendienteLocalDataSource {
    override suspend fun getOperacionesPendientes(): List<OperacionPendiente> {
        return dao.getOperacionesPendientes()
    }

    override suspend fun insertOperacionPendiente(
        tipoAccion: String,
        tablaAfectada: String,
        idAfectado: Long,
        datosJson: String,
        timestampCreacion: Long,
        sincronizado: Long
    ) {
        dao.insertOperacionPendiente(
            tipoAccion = tipoAccion,
            tablaAfectada = tablaAfectada,
            idAfectado = idAfectado,
            datosJson = datosJson,
            timestampCreacion = timestampCreacion,
            sincronizado = sincronizado
        )
    }

    override suspend fun deleteOperacionPendientePorEstado(estado: Long) {
        dao.deleteOperacionPendientePorEstado(estado)
    }

    override suspend fun cambiarEstadoOperacion(
        sincronizado: Long,
        idOperacion: Long
    ) {
        dao.cambiarEstadoOperacion(
            sincronizado = sincronizado,
            idOperacion = idOperacion
        )
    }

    override suspend fun deleteOperacionPendiente(idOperacion: Long) {
        dao.deleteOperacionPendiente(idOperacion)
    }

}