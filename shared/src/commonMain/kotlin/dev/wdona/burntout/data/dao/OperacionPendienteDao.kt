package dev.wdona.burntout.data.dao

import dev.wdona.burntout.domain.model.OperacionPendiente
import dev.wdona.burntout.shared.db.OperacionPendienteEntity

interface OperacionPendienteDao {
    suspend fun getOperacionesPendientes(): List<OperacionPendiente>;
    suspend fun insertOperacionPendiente(tipoAccion: String, tablaAfectada: String, idAfectado: String, datosJson: String, timestampCreacion: Long, sincronizado: Long = 0L);
    suspend fun deleteOperacionPendientePorEstado(estado: Long);
    suspend fun cambiarEstadoOperacion(sincronizado: Long, idOperacion: Long);
    suspend fun deleteOperacionPendiente(idOperacion: Long);

}