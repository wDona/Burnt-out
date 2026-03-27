package dev.wdona.burntout.data.datasource.mapper

import dev.wdona.burntout.domain.model.Ajuste
import dev.wdona.burntout.shared.db.GetAjusteByIdYUsuario
import dev.wdona.burntout.shared.db.GetAjustesByUsuario
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object AjusteMapper {
    fun toJson(ajuste: Ajuste): String {
        return Json.encodeToString(ajuste)
    }

    fun toDomain(entity: GetAjusteByIdYUsuario): Ajuste {
        return Ajuste(
            idAjuste = entity.ID_Ajuste,
            nombre = entity.Nombre_Ajuste,
            valorAjuste = entity.Valor_Ajuste ?: "NULL"
        )
    }

    fun toDomainFromGetAjusteByIdYUsuario(entityList: List<GetAjusteByIdYUsuario>): List<Ajuste> {
        return entityList.map { toDomain(it) }
    }

    fun toDomain(entity: GetAjustesByUsuario): Ajuste {
        return Ajuste(
            idAjuste = entity.ID_Ajuste,
            nombre = entity.Nombre_Ajuste,
            valorAjuste = entity.Valor_Ajuste ?: "NULL"
        )
    }

    fun toDomainFromGetAjustesByUsuario(entity: List<GetAjustesByUsuario>): List<Ajuste> {
        return entity.map { toDomain(it) }
    }
}
