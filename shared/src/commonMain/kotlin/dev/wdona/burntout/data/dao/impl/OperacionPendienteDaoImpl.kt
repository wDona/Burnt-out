package dev.wdona.burntout.data.dao.impl

import dev.wdona.burntout.data.dao.OperacionPendienteDao
import dev.wdona.burntout.data.datasource.mapper.OperacionPendienteMapper
import dev.wdona.burntout.domain.model.OperacionPendiente
import dev.wdona.burntout.shared.db.AppDatabase
import dev.wdona.burntout.shared.utils.Logger

class OperacionPendienteDaoImpl(appDatabase: AppDatabase) : OperacionPendienteDao {
    private val queries = appDatabase.appDatabaseQueries
    private val TAG = "OperacionPendienteDaoImpl"

    override suspend fun getOperacionesPendientes(): List<OperacionPendiente> {
        Logger.d(TAG, "getOperacionesPendientes")
        return queries.getOperacionesPendientes(50).executeAsList().map {
            OperacionPendienteMapper.toDomain(it)
        }
    }

    override suspend fun insertOperacionPendiente(tipoAccion: String, tablaAfectada: String, idAfectado: Long, datosJson: String, timestampCreacion: Long, sincronizado: Long) {
        Logger.d(TAG, "insertOperacionPendiente: tipo=$tipoAccion, tabla=$tablaAfectada, id=$idAfectado, json=$datosJson")
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
        Logger.d(TAG, "deleteOperacionPendientePorEstado: $estado")
        queries.deleteOperacionesPorEstado(estado)
    }

    override suspend fun cambiarEstadoOperacion(sincronizado: Long, idOperacion: Long) {
        Logger.d(TAG, "cambiarEstadoOperacion: id=$idOperacion, estado=$sincronizado")
        queries.cambiarEstadoOperacionPendiente(
            sincronizado = sincronizado,
            ID_Operacion = idOperacion
        )
    }

    override suspend fun deleteOperacionPendiente(idOperacion: Long) {
        Logger.d(TAG, "deleteOperacionPendiente: $idOperacion")
        queries.deleteOperacionesPendientesById(idOperacion)
    }
}