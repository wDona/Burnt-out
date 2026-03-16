package dev.wdona.burntout.data.datasource.mapper

import dev.wdona.burntout.domain.json.UsuarioJsonFields
import dev.wdona.burntout.shared.domain.Usuario
import dev.wdona.burntout.shared.db.UsuarioEntity
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

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
        return buildJsonObject {
            put(UsuarioJsonFields.ID.nombreCampo, JsonPrimitive(usuario.idUsuario))
            put(UsuarioJsonFields.USERNAME.nombreCampo, JsonPrimitive(usuario.username))
            put(UsuarioJsonFields.CONTRASENA.nombreCampo, JsonPrimitive(usuario.password))
            put(UsuarioJsonFields.NOMBRE.nombreCampo, JsonPrimitive(usuario.nombre))
            usuario.riesgoBurnout?.let { put(UsuarioJsonFields.RIESGO_BURNOUT.nombreCampo, JsonPrimitive(it)) }
            put(UsuarioJsonFields.DESCRIPCION.nombreCampo, JsonPrimitive(usuario.descripcion ?: ""))
            put(UsuarioJsonFields.ID_ORGANIZACION.nombreCampo, JsonPrimitive(usuario.idOrganizacion))
        }.toString()
    }
}
