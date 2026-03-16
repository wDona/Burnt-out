package dev.wdona.burntout.data.datasource.local.impl

import dev.wdona.burntout.data.dao.TableroDao
import dev.wdona.burntout.data.datasource.local.TableroLocalDataSource
import dev.wdona.burntout.shared.domain.Tablero

class TableroLocalDataSourceImpl(private val tableroDao: TableroDao) : TableroLocalDataSource {
    override suspend fun getTableroById(idTablero: Long): Tablero {
        return tableroDao.getTableroById(idTablero)
    }

    override suspend fun getTablerosByOrg(idOrg: Long): List<Tablero> {
        return tableroDao.getTablerosByOrg(idOrg)
    }

    override suspend fun crearTablero(tablero: Tablero): Long {
        return tableroDao.crearTablero(tablero)
    }

    override suspend fun actualizarTablero(tablero: Tablero): Boolean {
        return tableroDao.actualizarTablero(tablero)
    }

    override suspend fun eliminarTablero(idTablero: Long): Boolean {
        return tableroDao.eliminarTablero(idTablero)
    }

    override suspend fun insertOrUpdateTablero(tablero: Tablero): Boolean {
        return tableroDao.insertOrUpdateTablero(tablero)
    }

    override suspend fun eliminarTablerosPorOrg(idOrg: Long) {
        val tableros = tableroDao.getTablerosByOrg(idOrg)
        tableros.forEach { tableroDao.eliminarTablero(it.idTablero) }
    }
}
