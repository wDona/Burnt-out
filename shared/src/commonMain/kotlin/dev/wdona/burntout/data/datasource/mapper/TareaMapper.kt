package dev.wdona.burntout.data.datasource.mapper

import app.cash.sqldelight.Query
import dev.wdona.burntout.shared.domain.Tarea
import dev.wdona.burntout.shared.db.TareaEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
            return Json.encodeToString(tarea)
        }
    }
}
