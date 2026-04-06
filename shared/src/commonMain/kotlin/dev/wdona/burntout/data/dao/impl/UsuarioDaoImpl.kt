package dev.wdona.burntout.data.dao.impl

import dev.wdona.burntout.data.dao.UsuarioDao
import dev.wdona.burntout.data.datasource.mapper.UsuarioMapper
import dev.wdona.burntout.shared.domain.Usuario
import dev.wdona.burntout.shared.db.AppDatabase

class UsuarioDaoImpl(appDatabase: AppDatabase) : UsuarioDao {
    private val queries = appDatabase.appDatabaseQueries

    override suspend fun getUserById(idUsuario: Long): Usuario {
        val entity = queries.getUserById(idUsuario).executeAsOne()
        return UsuarioMapper.toDomain(entity)
    }

    override suspend fun getUsuariosByOrg(idOrg: Long): List<Usuario> {
        return queries.getUsuariosByOrg(idOrg).executeAsList().map {
            UsuarioMapper.toDomain(it)
        }
    }

    override suspend fun getUsuariosByEquipo(idEquipo: Long): List<Usuario> {
        return queries.getUsuariosByEquipo(idEquipo).executeAsList().map {
            UsuarioMapper.toDomain(it)
        }
    }

    override suspend fun getUsuarioByUsername(username: String): Usuario {
        val entity = queries.getUsuarioByUsername(username).executeAsOne()
        return UsuarioMapper.toDomain(entity)
    }

    override suspend fun crearUsuario(usuario: Usuario): Long {
        queries.insertUsuario(
            Username = usuario.username,
            Contrasena = usuario.password,
            Nombre = usuario.nombre,
            Riesgo_Burnout = usuario.riesgoBurnout,
            Descripcion = usuario.descripcion,
            FK_ID_Organizacion = usuario.idOrganizacion,
            FK_ID_Equipo = usuario.idEquipo
        )
        return queries.lastInsertRowId().executeAsOne()
    }

    override suspend fun actualizarUsuario(usuario: Usuario): Boolean {
        return try {
            queries.updateUsuario(
                Username = usuario.username,
                Contrasena = usuario.password,
                Nombre = usuario.nombre,
                Riesgo_Burnout = usuario.riesgoBurnout,
                Descripcion = usuario.descripcion,
                FK_ID_Equipo = usuario.idEquipo,
                ID_Usuario = usuario.idUsuario
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun eliminarUsuario(idUsuario: Long): Boolean {
        return try {
            queries.deleteUsuario(idUsuario)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun insertOrUpdateUsuario(usuario: Usuario): Boolean {
        return try {
            queries.upsertUsuario(
                ID_Usuario = usuario.idUsuario,
                Username = usuario.username,
                Contrasena = usuario.password,
                Nombre = usuario.nombre,
                Riesgo_Burnout = usuario.riesgoBurnout,
                Descripcion = usuario.descripcion,
                FK_ID_Organizacion = usuario.idOrganizacion,
                FK_ID_Equipo = usuario.idEquipo
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun vincularUsuarioEquipo(idUsuario: Long, idEquipo: Long) {
        queries.transaction {
            queries.insertUserTeam(idUsuario, idEquipo)
            queries.updateUsuarioTeamId(idEquipo, idUsuario)
        }
    }

    override suspend fun updateRiesgoBurnout(idUsuario: Long, riesgo: Double): Boolean {
        return try {
            queries.updateRiesgoBurnout(riesgo, idUsuario)
            true
        } catch (e: Exception) {
            false
        }
    }
}
