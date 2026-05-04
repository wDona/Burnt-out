package dev.wdona.burntout.data.datasource.mapper

import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.db.EquipoEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object EquipoMapper {
    fun toDomain(entity: EquipoEntity): Equipo {
        return Equipo(
            idEquipo = entity.ID_Equipo,
            titulo = entity.Titulo,
            puntuacion = entity.Puntuacion ?: 0,
            idOrganizacion = entity.FK_ID_Org,
            idMiembros = emptyList(), // Miembros se cargan aparte o por relacion
            isDeleted = entity.Is_Deleted != 0L,
            updatedAt = entity.Updated_At
        )
    }

    fun toEntity(domain: Equipo): EquipoEntity {
        return EquipoEntity(
            ID_Equipo = domain.idEquipo,
            Titulo = domain.titulo,
            Puntuacion = domain.puntuacion,
            FK_ID_Org = domain.idOrganizacion,
            Is_Deleted = if (domain.isDeleted) 1L else 0L,
            Updated_At = domain.updatedAt
        )
    }

    fun toJson(equipo: Equipo): String {
        return Json.encodeToString(equipo)
    }
}
