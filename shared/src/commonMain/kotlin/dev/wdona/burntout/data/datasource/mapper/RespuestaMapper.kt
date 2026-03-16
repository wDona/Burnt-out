package dev.wdona.burntout.data.datasource.mapper

import dev.wdona.burntout.domain.json.RespuestaJsonFields
import dev.wdona.burntout.shared.domain.Respuesta
import dev.wdona.burntout.shared.db.ResponderEntity
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject


class RespuestaMapper {
    companion object {
        fun toDomain(entity: ResponderEntity): Respuesta {
            return Respuesta(
                idUsuario = entity.ID_Usuario,
                idPregunta = entity.ID_Pregunta,
                anonimo = entity.Anonimo == 1L,
                respuesta = entity.Respuesta
            )
        }

        fun toDomainList(list: List<ResponderEntity>): List<Respuesta> {
            return list.map { toDomain(it) }
        }

        fun toJson(respuesta: Respuesta): String {
            return buildJsonObject {
                put(RespuestaJsonFields.ID_USUARIO.nombreCampo, JsonPrimitive(respuesta.idUsuario))
                put(RespuestaJsonFields.ID_PREGUNTA.nombreCampo, JsonPrimitive(respuesta.idPregunta))
                put(RespuestaJsonFields.ANONIMO.nombreCampo, JsonPrimitive(respuesta.anonimo))
                put(RespuestaJsonFields.RESPUESTA.nombreCampo, JsonPrimitive(respuesta.respuesta))
                if (respuesta.nombreUsuario != null) {
                    put(RespuestaJsonFields.NOMBRE_USUARIO.nombreCampo, JsonPrimitive(respuesta.nombreUsuario))
                }
            }.toString()
        }
    }
}