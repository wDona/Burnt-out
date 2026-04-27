package dev.wdona.burntout.data.datasource.mapper

import dev.wdona.burntout.domain.json.TableroJsonFields
import dev.wdona.burntout.shared.domain.Tablero
import dev.wdona.burntout.shared.db.TableroEntity
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

object TableroMapper {
    fun toDomain(entity: TableroEntity): Tablero {
        return Tablero(
            idTablero = entity.ID_Tabl,
            titulo = entity.Titulo,
            idOrganizacion = entity.FK_ID_Org,
            idEquipo = entity.FK_ID_Equipo,
            isDeleted = entity.Is_Deleted != 0L
        )
    }

    fun toEntity(domain: Tablero): TableroEntity {
        return TableroEntity(
            ID_Tabl = domain.idTablero,
            Titulo = domain.titulo,
            FK_ID_Org = domain.idOrganizacion,
            FK_ID_Equipo = domain.idEquipo,
            Is_Deleted = if (domain.isDeleted) 1L else 0L
        )
    }

    fun toJson(tablero: Tablero): String {
        return buildJsonObject {
            put(TableroJsonFields.ID.nombreCampo, JsonPrimitive(tablero.idTablero))
            put(TableroJsonFields.TITULO.nombreCampo, JsonPrimitive(tablero.titulo))
            put(TableroJsonFields.ID_ORGANIZACION.nombreCampo, JsonPrimitive(tablero.idOrganizacion))
            tablero.idEquipo?.let { 
                put(TableroJsonFields.ID_EQUIPO.nombreCampo, JsonPrimitive(it)) 
            }
        }.toString()
    }
}
