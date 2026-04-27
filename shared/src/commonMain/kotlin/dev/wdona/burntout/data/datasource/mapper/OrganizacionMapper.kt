package dev.wdona.burntout.data.datasource.mapper

import dev.wdona.burntout.shared.db.OrganizacionEntity
import dev.wdona.burntout.shared.domain.Organizacion
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object OrganizacionMapper {
    fun toDomain(entity: OrganizacionEntity): Organizacion {
        return Organizacion(
            idOrganizacion = entity.ID_Org,
            nombre = entity.Org_Name,
            isDeleted = entity.Is_Deleted != 0L
        )
    }

    fun toJson(organizacion: Organizacion): String {
        return Json.encodeToString(organizacion)
    }
}