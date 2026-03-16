package dev.wdona.burntout.data.dao.impl

import dev.wdona.burntout.data.dao.OperacionPendienteDao
import dev.wdona.burntout.data.datasource.mapper.OperacionPendienteMapper
import dev.wdona.burntout.domain.model.OperacionPendiente
import dev.wdona.burntout.shared.db.AppDatabase

class OperacionPendienteDaoImpl(appDatabase: AppDatabase) : OperacionPendienteDao {
    private val queries = appDatabase.appDatabaseQueries

    override suspend fun getOperacionesPendientes(): List<OperacionPendiente> {
        return queries.getOperacionesPendientes(50).executeAsList().map {
            OperacionPendienteMapper.toDomain(it)
        }
    }

    override suspend fun insertOperacionPendiente(tipoAccion: String, tablaAfectada: String, idAfectado: Long, datosJson: String, timestampCreacion: Long, sincronizado: Long) {
        queries.insertOperacionPendiente(
            tipo_accion = tipoAccion,
            tabla_afectada = tablaAfectada,
            id_afectado = idAfectado,
            datos_json = datosJson,
            timestamp_creacion = timestampCreacion,
            sincronizado = sincronizado
        )
    }

    override suspend fun deleteOperacionPendientePorEstado(estado: Long) {
        queries.deleteOperacionesPorEstado(estado)
    }

    override suspend fun cambiarEstadoOperacion(sincronizado: Long, idOperacion: Long) {
        queries.cambiarEstadoOperacionPendiente(
            sincronizado = sincronizado,
            ID_Operacion = idOperacion
        )
    }

    override suspend fun deleteOperacionPendiente(idOperacion: Long) {
        queries.deleteOperacionesPendientesById(idOperacion)
    }
}