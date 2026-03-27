package dev.wdona.burntout.data.datasource.mapper

import dev.wdona.burntout.shared.domain.Usuario
import dev.wdona.burntout.shared.db.UsuarioEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object UsuarioMapper {
    fun toDomain(entity: UsuarioEntity, idEquipo: Long): Usuario {
        return Usuario(
            idUsuario = entity.ID_Usuario,
            username = entity.Username,
            password = entity.Contrasena,
            nombre = entity.Nombre,
            riesgoBurnout = entity.Riesgo_Burnout,
            descripcion = entity.Descripcion,
            idOrganizacion = entity.FK_ID_Organizacion,
            idEquipo = idEquipo
        )
    }

    fun toEntity(domain: Usuario): UsuarioEntity {
        return UsuarioEntity(
            ID_Usuario = domain.idUsuario,
            Username = domain.username,
            Contrasena = domain.password,
            Nombre = domain.nombre,
            Riesgo_Burnout = domain.riesgoBurnout,
            Descripcion = domain.descripcion,
            FK_ID_Organizacion = domain.idOrganizacion
        )
    }

    fun toJson(usuario: Usuario): String {
        return Json.encodeToString(usuario)
    }
}
