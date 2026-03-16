package dev.wdona.burntout.data.datasource.mapper

import app.cash.sqldelight.Query
import dev.wdona.burntout.domain.json.TareaJsonFields
import dev.wdona.burntout.shared.domain.Tarea
import dev.wdona.burntout.shared.db.TareaEntity
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.JsonPrimitive

class TareaMapper {
    companion object {
        fun toDomain(tareaEntity: TareaEntity, idSubtareas: List<Long>): Tarea {
            return Tarea(
                idTarea = tareaEntity.ID_Tarea,
                titulo = tareaEntity.Titulo,
                descripcion = tareaEntity.Descripcion,
                estado = tareaEntity.Estado,
                idTableroPerteneciente = tareaEntity.FK_ID_Tabl,
                idUsuarioAsignado = tareaEntity.FK_ID_Usuario,
                idSubtareas = idSubtareas
            )
        }

        fun toEntity(tarea: Tarea): TareaEntity {
            return TareaEntity(
                ID_Tarea = tarea.idTarea,
                Titulo = tarea.titulo,
                Descripcion = tarea.descripcion,
                Estado = tarea.estado,
                FK_ID_Tabl = tarea.idTableroPerteneciente,
                FK_ID_Usuario = tarea.idUsuarioAsignado
            )
        }

        fun toDomain(tareaEntity: TareaEntity): Tarea {
            return Tarea(
                idTarea = tareaEntity.ID_Tarea,
                titulo = tareaEntity.Titulo,
                descripcion = tareaEntity.Descripcion,
                estado = tareaEntity.Estado,
                idTableroPerteneciente = tareaEntity.FK_ID_Tabl,
                idUsuarioAsignado = tareaEntity.FK_ID_Usuario,
                idSubtareas = emptyList()
            )
        }

        fun toDomainList(tareaEntities: Query<TareaEntity>): List<Tarea> {
            return tareaEntities.executeAsList().map { toDomain(it) }
        }

        fun toDomain(tareaEntity: Query<TareaEntity>): Tarea {
            return toDomain(tareaEntity.executeAsOne())
        }

        fun toJson(tarea: Tarea): String {
            return buildJsonObject {
                put(TareaJsonFields.ID.nombreCampo, JsonPrimitive(tarea.idTarea))
                put(TareaJsonFields.NOMBRE.nombreCampo, JsonPrimitive(tarea.titulo))
                put(TareaJsonFields.DESCRIPCION.nombreCampo, JsonPrimitive(tarea.descripcion ?: ""))
                put(TareaJsonFields.ESTADO.nombreCampo, JsonPrimitive(tarea.estado))
                put(TareaJsonFields.ID_TABLERO.nombreCampo, JsonPrimitive(tarea.idTableroPerteneciente))
                put(TareaJsonFields.ID_USUARIO_ASIGNADO.nombreCampo, JsonPrimitive(tarea.idUsuarioAsignado))
                put(TareaJsonFields.ID_SUBTAREAS.nombreCampo, JsonPrimitive(tarea.idSubtareas.toString()))
            }.toString()
        }
    }
}
