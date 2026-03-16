package dev.wdona.burntout.data.datasource.local

import dev.wdona.burntout.domain.model.OperacionPendiente

interface OperacionPendienteLocalDataSource {
    suspend fun getOperacionesPendientes(): List<OperacionPendiente>;
    suspend fun insertOperacionPendiente(tipoAccion: String, tablaAfectada: String, idAfectado: Long, datosJson: String, timestampCreacion: Long, sincronizado: Long = 0L);
    suspend fun deleteOperacionPendientePorEstado(estado: Long);
    suspend fun cambiarEstadoOperacion(sincronizado: Long, idOperacion: Long);
    suspend fun deleteOperacionPendiente(idOperacion: Long);
}