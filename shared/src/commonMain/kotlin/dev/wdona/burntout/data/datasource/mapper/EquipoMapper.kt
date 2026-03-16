package dev.wdona.burntout.data.datasource.mapper

import dev.wdona.burntout.domain.json.EquipoJsonFields
import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.db.EquipoEntity
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

object EquipoMapper {
    fun toDomain(entity: EquipoEntity): Equipo {
        return Equipo(
            idEquipo = entity.ID_Equipo,
            titulo = entity.Titulo,
            puntuacion = entity.Puntuacion ?: 0,
            idOrganizacion = entity.FK_ID_Org,
            idMiembros = emptyList() // Miembros se cargan aparte o por relacion
        )
    }

    fun toEntity(domain: Equipo): EquipoEntity {
        return EquipoEntity(
            ID_Equipo = domain.idEquipo,
            Titulo = domain.titulo,
            Puntuacion = domain.puntuacion,
            FK_ID_Org = domain.idOrganizacion
        )
    }

    fun toJson(equipo: Equipo): String {
        return buildJsonObject {
            put(EquipoJsonFields.ID.nombreCampo, JsonPrimitive(equipo.idEquipo))
            put(EquipoJsonFields.TITULO.nombreCampo, JsonPrimitive(equipo.titulo))
            put(EquipoJsonFields.PUNTUACION.nombreCampo, JsonPrimitive(equipo.puntuacion ?: 0))
            put(EquipoJsonFields.ID_ORGANIZACION.nombreCampo, JsonPrimitive(equipo.idOrganizacion))
        }.toString()
    }
}
