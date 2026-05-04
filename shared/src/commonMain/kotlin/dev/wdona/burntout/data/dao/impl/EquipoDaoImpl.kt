package dev.wdona.burntout.data.dao.impl

import dev.wdona.burntout.data.dao.EquipoDao
import dev.wdona.burntout.data.datasource.mapper.EquipoMapper
import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.db.AppDatabase
import dev.wdona.burntout.shared.utils.Logger
import dev.wdona.burntout.shared.utils.getCurrentTimestampSeconds

class EquipoDaoImpl(appDatabase: AppDatabase) : EquipoDao {
    private val queries = appDatabase.appDatabaseQueries
    private val TAG = "EquipoDaoImpl"

    override suspend fun getEquipoById(idEquipo: Long): Equipo {
        Logger.d(TAG, "getEquipoById: $idEquipo")
        val entity = queries.getEquipoById(idEquipo).executeAsOne()
        return EquipoMapper.toDomain(entity)
    }

    override suspend fun getEquiposByOrg(idOrg: Long): List<Equipo> {
        Logger.d(TAG, "getEquiposByOrg: $idOrg")
        return queries.getEquiposByOrg(idOrg).executeAsList().map {
            EquipoMapper.toDomain(it)
        }
    }

    override suspend fun crearEquipo(equipo: Equipo): Long {
        Logger.d(TAG, "crearEquipo: ${equipo.titulo}")
        queries.insertEquipo(
            equipo.titulo,
            equipo.idOrganizacion,
            getCurrentTimestampSeconds()
        )
        return queries.lastInsertRowId().executeAsOne()
    }

    override suspend fun insertOrUpdateEquipo(equipo: Equipo): Boolean {
        Logger.d(TAG, "insertOrUpdateEquipo: ${equipo.idEquipo}")
        return try {
            queries.upsertEquipo(
                equipo.idEquipo,
                equipo.titulo,
                equipo.puntuacion ?: 0,
                equipo.idOrganizacion,
                if (equipo.isDeleted) 1L else 0L,
                equipo.updatedAt
            )
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error insertOrUpdateEquipo: ${e.message}")
            false
        }
    }

    override suspend fun actualizarEquipo(equipo: Equipo): Boolean {
        Logger.d(TAG, "actualizarEquipo: ${equipo.idEquipo}")
        return try {
            queries.updateEquipo(
                equipo.titulo,
                getCurrentTimestampSeconds(),
                equipo.idEquipo
            )
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error actualizarEquipo: ${e.message}")
            false
        }
    }

    override suspend fun eliminarEquipo(idEquipo: Long): Boolean {
        Logger.d(TAG, "eliminarEquipo: $idEquipo")
        return try {
            queries.deleteEquipo(
                getCurrentTimestampSeconds(),
                idEquipo
            )
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error eliminarEquipo: ${e.message}")
            false
        }
    }

    override suspend fun updatePuntuacion(idEquipo: Long, puntos: Long): Boolean {
        Logger.d(TAG, "updatePuntuacion: equipo=$idEquipo, puntos=$puntos")
        return try {
            queries.updatePuntuacionEquipo(
                puntos,
                getCurrentTimestampSeconds(),
                idEquipo
            )
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error updatePuntuacion: ${e.message}")
            false
        }
    }

    override suspend fun addUsuarioAlEquipo(idEquipo: Long, idUsuario: Long): Boolean {
        Logger.d(TAG, "addUsuarioAlEquipo: equipo=$idEquipo, user=$idUsuario")
        val now = getCurrentTimestampSeconds()
        return try {
            queries.transaction {
                queries.deleteUserFromAllTeams(now, idUsuario)
                queries.insertUserTeam(idUsuario, idEquipo, now)
                queries.updateUsuarioTeamId(idEquipo, now, idUsuario)
                queries.deleteEmptyTeams(now)
            }
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error addUsuarioAlEquipo: ${e.message}")
            false
        }
    }

    override suspend fun removeUsuarioDelEquipo(idEquipo: Long, idUsuario: Long): Boolean {
        Logger.d(TAG, "removeUsuarioDelEquipo: equipo=$idEquipo, user=$idUsuario")
        val now = getCurrentTimestampSeconds()
        return try {
            queries.transaction {
                queries.deleteUserTeam(now, idUsuario, idEquipo)
                queries.updateUsuarioTeamId(0L, now, idUsuario) // 0 o null segun convención
                queries.deleteEmptyTeams(now)
            }
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error removeUsuarioDelEquipo: ${e.message}")
            false
        }
    }
}
