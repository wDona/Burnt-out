package dev.wdona.burntout.data.dao.impl

import dev.wdona.burntout.data.dao.EquipoDao
import dev.wdona.burntout.data.datasource.mapper.EquipoMapper
import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.db.AppDatabase

class EquipoDaoImpl(appDatabase: AppDatabase) : EquipoDao {
    private val queries = appDatabase.appDatabaseQueries

    override suspend fun getEquipoById(idEquipo: Long): Equipo {
        val entity = queries.getEquipoById(idEquipo).executeAsOne()
        return EquipoMapper.toDomain(entity)
    }

    override suspend fun getEquiposByOrg(idOrg: Long): List<Equipo> {
        return queries.getEquiposByOrg(idOrg).executeAsList().map {
            EquipoMapper.toDomain(it)
        }
    }

    override suspend fun crearEquipo(equipo: Equipo): Long {
        queries.insertEquipo(
            equipo.titulo,
            equipo.idOrganizacion
        )
        return queries.lastInsertRowId().executeAsOne()
    }

    override suspend fun insertOrUpdateEquipo(equipo: Equipo): Boolean {
        return try {
            queries.upsertEquipo(
                equipo.idEquipo,
                equipo.titulo,
                equipo.puntuacion ?: 0,
                equipo.idOrganizacion
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun actualizarEquipo(equipo: Equipo): Boolean {
        return try {
            queries.updateEquipo(
                equipo.titulo,
                equipo.idEquipo
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun eliminarEquipo(idEquipo: Long): Boolean {
        return try {
            queries.deleteEquipo(idEquipo)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updatePuntuacion(idEquipo: Long, puntos: Long): Boolean {
        return try {
            queries.updatePuntuacionEquipo(puntos, idEquipo)
            true
        } catch (e: Exception) {
            false
        }
    }
}
