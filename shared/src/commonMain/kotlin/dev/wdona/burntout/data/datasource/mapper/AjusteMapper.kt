package dev.wdona.burntout.data.datasource.mapper

import dev.wdona.burntout.domain.model.Ajuste
import dev.wdona.burntout.shared.db.AjusteEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object AjusteMapper {
    fun toJson(ajuste: Ajuste): String {
        return Json.encodeToString(ajuste)
    }

    fun toDomain(entity: AjusteEntity): Ajuste {
        return Ajuste(
            idAjuste = entity.ID_Ajuste,
            idUsuario = entity.FK_ID_Usuario,
            nombre = entity.Nombre_Ajuste,
            valorAjuste = entity.Valor_Ajuste,
            isDeleted = entity.Is_Deleted == 1L,
            updatedAt = entity.Updated_At
        )
    }

    fun toDomainList(entities: List<AjusteEntity>): List<Ajuste> {
        return entities.map { toDomain(it) }
    }
}
