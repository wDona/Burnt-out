package dev.wdona.burntout.data.dao.impl

import dev.wdona.burntout.data.dao.UsuarioDao
import dev.wdona.burntout.data.datasource.mapper.UsuarioMapper
import dev.wdona.burntout.shared.domain.Usuario
import dev.wdona.burntout.shared.db.AppDatabase
import dev.wdona.burntout.shared.utils.Logger
import dev.wdona.burntout.shared.utils.getCurrentTimestampSeconds

class UsuarioDaoImpl(appDatabase: AppDatabase) : UsuarioDao {
    private val queries = appDatabase.appDatabaseQueries
    private val TAG = "UsuarioDaoImpl"

    override suspend fun getUserById(idUsuario: Long): Usuario {
        Logger.d(TAG, "getUserById: $idUsuario")
        val entity = queries.getUserById(idUsuario).executeAsOne()
        return UsuarioMapper.toDomain(entity)
    }

    override suspend fun getUsuariosByOrg(idOrg: Long): List<Usuario> {
        Logger.d(TAG, "getUsuariosByOrg: $idOrg")
        return queries.getUsuariosByOrg(idOrg).executeAsList().map {
            UsuarioMapper.toDomain(it)
        }
    }


    override suspend fun getUsuariosByEquipo(idEquipo: Long): List<Usuario> {
        Logger.d(TAG, "getUsuariosByEquipo: $idEquipo")
        return queries.getUsuariosByEquipo(idEquipo).executeAsList().map {
            UsuarioMapper.toDomain(it)
        }
    }

    override suspend fun getUsuarioByUsername(username: String): Usuario {
        Logger.d(TAG, "getUsuarioByUsername: $username")
        val entity = queries.getUsuarioByUsername(username).executeAsOne()
        return UsuarioMapper.toDomain(entity)
    }

    override suspend fun crearUsuario(usuario: Usuario): Long {
        Logger.d(TAG, "crearUsuario: $usuario")
        queries.insertUsuario(
            Username = usuario.username,
            Contrasena = usuario.password,
            Nombre = usuario.nombre,
            Riesgo_Burnout = usuario.riesgoBurnout,
            Descripcion = usuario.descripcion,
            FK_ID_Organizacion = usuario.idOrganizacion,
            FK_ID_Equipo = usuario.idEquipo,
            Rol = usuario.rol,
            Updated_At = usuario.updatedAt.takeIf { it > 0L } ?: getCurrentTimestampSeconds()
        )
        return queries.lastInsertRowId().executeAsOne()
    }

    override suspend fun actualizarUsuario(usuario: Usuario): Boolean {
        Logger.d(TAG, "actualizarUsuario: $usuario")
        return try {
            queries.updateUsuario(
                Username = usuario.username,
                Contrasena = usuario.password,
                Nombre = usuario.nombre,
                Riesgo_Burnout = usuario.riesgoBurnout,
                Descripcion = usuario.descripcion,
                FK_ID_Equipo = usuario.idEquipo,
                Rol = usuario.rol,
                Updated_At = usuario.updatedAt.takeIf { it > 0L } ?: getCurrentTimestampSeconds(),
                ID_Usuario = usuario.idUsuario
            )
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error actualizarUsuario: ${e.message}")
            false
        }
    }

    override suspend fun eliminarUsuario(idUsuario: Long): Boolean {
        Logger.d(TAG, "eliminarUsuario: $idUsuario")
        return try {
            queries.deleteUsuario(
                Updated_At = getCurrentTimestampSeconds(),
                ID_Usuario = idUsuario
            )
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error eliminarUsuario: ${e.message}")
            false
        }
    }

    override suspend fun insertOrUpdateUsuario(usuario: Usuario): Boolean {
        Logger.d(TAG, "insertOrUpdateUsuario: $usuario")
        return try {
            queries.upsertUsuario(
                ID_Usuario = usuario.idUsuario,
                Username = usuario.username,
                Contrasena = usuario.password,
                Nombre = usuario.nombre,
                Riesgo_Burnout = usuario.riesgoBurnout,
                Descripcion = usuario.descripcion,
                FK_ID_Organizacion = usuario.idOrganizacion,
                FK_ID_Equipo = usuario.idEquipo,
                Rol = usuario.rol,
                Is_Deleted = if (usuario.isDeleted) 1L else 0L,
                Updated_At = usuario.updatedAt.takeIf { it > 0L } ?: getCurrentTimestampSeconds()
            )
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error insertOrUpdateUsuario: ${e.message}")
            false
        }
    }

    override suspend fun vincularUsuarioEquipo(idUsuario: Long, idEquipo: Long) {
        Logger.d(TAG, "vincularUsuarioEquipo: user=$idUsuario, equipo=$idEquipo")
        val now = getCurrentTimestampSeconds()
        queries.transaction {
            queries.insertUserTeam(idUsuario, idEquipo, now)
            queries.updateUsuarioTeamId(idEquipo, now, idUsuario)
        }
    }

    override suspend fun updateRiesgoBurnout(idUsuario: Long, riesgo: Double): Boolean {
        Logger.d(TAG, "updateRiesgoBurnout: user=$idUsuario, riesgo=$riesgo")
        return try {
            queries.updateRiesgoBurnout(
                Riesgo_Burnout = riesgo,
                Updated_At = getCurrentTimestampSeconds(),
                ID_Usuario = idUsuario
            )
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error updateRiesgoBurnout: ${e.message}")
            false
        }
    }
}
