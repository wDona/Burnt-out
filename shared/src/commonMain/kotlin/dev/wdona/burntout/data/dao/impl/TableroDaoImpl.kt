package dev.wdona.burntout.data.dao.impl

import dev.wdona.burntout.data.dao.TableroDao
import dev.wdona.burntout.data.datasource.mapper.TableroMapper
import dev.wdona.burntout.shared.domain.Tablero
import dev.wdona.burntout.shared.db.AppDatabase
import dev.wdona.burntout.shared.utils.Logger

class TableroDaoImpl(appDatabase: AppDatabase) : TableroDao {
    private val queries = appDatabase.appDatabaseQueries
    private val TAG = "TableroDaoImpl"

    override suspend fun getTableroById(idTablero: Long): Tablero {
        Logger.d(TAG, "getTableroById: $idTablero")
        val entity = queries.getTableroById(idTablero).executeAsOne()
        return TableroMapper.toDomain(entity)
    }

    override suspend fun getTablerosByOrg(idOrg: Long): List<Tablero> {
        Logger.d(TAG, "getTablerosByOrg: $idOrg")
        return queries.getTablerosByOrg(idOrg).executeAsList().map {
            TableroMapper.toDomain(it)
        }
    }

    override suspend fun crearTablero(tablero: Tablero): Long {
        Logger.d(TAG, "crearTablero: $tablero")
        queries.insertTablero(
            tablero.titulo,
            tablero.idEquipo,
            tablero.idOrganizacion
        )
        return queries.lastInsertRowId().executeAsOne()
    }

    override suspend fun actualizarTablero(tablero: Tablero): Boolean {
        Logger.d(TAG, "actualizarTablero: $tablero")
        return try {
            queries.updateTablero(
                tablero.titulo,
                tablero.idEquipo,
                tablero.idTablero
            )
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error actualizarTablero: ${e.message}")
            false
        }
    }

    override suspend fun eliminarTablero(idTablero: Long): Boolean {
        Logger.d(TAG, "eliminarTablero: $idTablero")
        return try {
            queries.deleteTablero(idTablero)
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error eliminarTablero: ${e.message}")
            false
        }
    }

    override suspend fun insertOrUpdateTablero(tablero: Tablero): Boolean {
        Logger.d(TAG, "insertOrUpdateTablero: $tablero")
        return try {
            queries.upsertTablero(
                tablero.idTablero,
                tablero.titulo,
                tablero.idEquipo,
                tablero.idOrganizacion
            )
            true
        } catch (e: Exception) {
            Logger.d(TAG, "Error insertOrUpdateTablero: ${e.message}")
            false
        }
    }
}
