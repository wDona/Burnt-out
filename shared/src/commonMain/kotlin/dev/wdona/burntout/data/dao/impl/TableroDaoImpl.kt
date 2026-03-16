package dev.wdona.burntout.data.dao.impl

import dev.wdona.burntout.data.dao.TableroDao
import dev.wdona.burntout.data.datasource.mapper.TableroMapper
import dev.wdona.burntout.shared.domain.Tablero
import dev.wdona.burntout.shared.db.AppDatabase

class TableroDaoImpl(appDatabase: AppDatabase) : TableroDao {
    private val queries = appDatabase.appDatabaseQueries

    override suspend fun getTableroById(idTablero: Long): Tablero {
        val entity = queries.getTableroById(idTablero).executeAsOne()
        return TableroMapper.toDomain(entity)
    }

    override suspend fun getTablerosByOrg(idOrg: Long): List<Tablero> {
        return queries.getTablerosByOrg(idOrg).executeAsList().map {
            TableroMapper.toDomain(it)
        }
    }

    override suspend fun crearTablero(tablero: Tablero): Long {
        queries.insertTablero(
            tablero.titulo,
            tablero.idEquipo,
            tablero.idOrganizacion
        )
        return queries.lastInsertRowId().executeAsOne()
    }

    override suspend fun actualizarTablero(tablero: Tablero): Boolean {
        return try {
            queries.updateTablero(
                tablero.titulo,
                tablero.idEquipo,
                tablero.idTablero
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun eliminarTablero(idTablero: Long): Boolean {
        return try {
            queries.deleteTablero(idTablero)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun insertOrUpdateTablero(tablero: Tablero): Boolean {
        return try {
            queries.upsertTablero(
                tablero.idTablero,
                tablero.titulo,
                tablero.idEquipo,
                tablero.idOrganizacion
            )
            true
        } catch (e: Exception) {
            false
        }
    }
}
