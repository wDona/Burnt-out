package dev.wdona.burntout.data.datasource.mapper

import dev.wdona.burntout.domain.json.PreguntaJsonFields
import dev.wdona.burntout.shared.db.PreguntaEntity
import dev.wdona.burntout.shared.domain.Pregunta
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class PreguntaMapper {
    companion object {
        fun toDomain(entity: PreguntaEntity): Pregunta {
            return Pregunta(
                idPregunta = entity.ID_Pregunta,
                pregunta = entity.Pregunta,
                idOrganizacion = entity.FK_ID_Org,
                categoria = entity.Categoria,
                isDeleted = entity.Is_Deleted != 0L,
                updatedAt = entity.Updated_At
            )
        }

        fun toDomainList(list: List<PreguntaEntity>): List<Pregunta> {
            return list.map { toDomain(it) }
        }

        fun toJson(pregunta: Pregunta): String {
            return buildJsonObject {
                put(PreguntaJsonFields.ID_PREGUNTA.nombreCampo, JsonPrimitive(pregunta.idPregunta))
                put(PreguntaJsonFields.PREGUNTA.nombreCampo, JsonPrimitive(pregunta.pregunta))
                put(PreguntaJsonFields.ID_ORGANIZACION.nombreCampo, JsonPrimitive(pregunta.idOrganizacion))
                put("categoria", JsonPrimitive(pregunta.categoria))
                put("updated_at", JsonPrimitive(pregunta.updatedAt))
                put("is_deleted", JsonPrimitive(if (pregunta.isDeleted) 1 else 0))
            }.toString()
        }
    }
}