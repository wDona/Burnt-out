package dev.wdona.burntout.data.datasource.mapper

import dev.wdona.burntout.domain.model.OperacionPendiente
import dev.wdona.burntout.shared.db.OperacionPendienteEntity

object OperacionPendienteMapper {
    fun toEntity(operacionPendiente: OperacionPendiente) : OperacionPendienteEntity {
        return OperacionPendienteEntity(
            ID_Operacion = operacionPendiente.idAccion,
            tipo_accion = operacionPendiente.tipoAccion,
            tabla_afectada = operacionPendiente.tablaAfectada,
            id_afectado = operacionPendiente.idAfectado,
            datos_json = operacionPendiente.datosJson,
            timestamp_creacion = operacionPendiente.timestampCreacion,
            sincronizado = if (operacionPendiente.sincronizado) 1 else 0
        )
    }

    fun toDomain(operacionPendienteEntity: OperacionPendienteEntity) : OperacionPendiente {
        return OperacionPendiente(
            idAccion = operacionPendienteEntity.ID_Operacion,
            tipoAccion = operacionPendienteEntity.tipo_accion,
            tablaAfectada = operacionPendienteEntity.tabla_afectada,
            idAfectado = operacionPendienteEntity.id_afectado,
            datosJson = operacionPendienteEntity.datos_json,
            timestampCreacion = operacionPendienteEntity.timestamp_creacion,
            sincronizado = operacionPendienteEntity.sincronizado == 1L
        )
    }

}