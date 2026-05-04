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
            isDeleted = entity.Is_Deleted != 0L,
            updatedAt = entity.Updated_At
        )
    }

    fun toEntity(domain: Organizacion): OrganizacionEntity {
        return OrganizacionEntity(
            ID_Org = domain.idOrganizacion,
            Org_Name = domain.nombre,
            Is_Deleted = if (domain.isDeleted) 1L else 0L,
            Updated_At = domain.updatedAt
        )
    }

    fun toJson(organizacion: Organizacion): String {
        return Json.encodeToString(organizacion)
    }
}