package dev.wdona.burntout.data.datasource.mapper

import dev.wdona.burntout.shared.db.SubtareaEntity
import dev.wdona.burntout.shared.domain.Subtarea
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object SubtareaMapper {
    fun toDomain(entity: SubtareaEntity): Subtarea {
        return Subtarea(
            idSubtarea = entity.ID_Subtarea,
            titulo = entity.Titulo,
            descripcion = null,
            completado = entity.Completado != 0L,
            idTareaPerteneciente = entity.FK_ID_Tarea
        )
    }

    fun toJson(subtarea: Subtarea): String {
        return Json.encodeToString(subtarea)
    }
}