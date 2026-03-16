package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import dev.wdona.burntout.data.api.impl.TableroApiImpl
import dev.wdona.burntout.data.dao.impl.OperacionPendienteDaoImpl
import dev.wdona.burntout.data.dao.impl.TableroDaoImpl
import dev.wdona.burntout.data.datasource.local.impl.OperacionPendienteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.TableroLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.TableroRemoteDataSourceImpl
import dev.wdona.burntout.data.repository.TableroRepositoryImpl
import dev.wdona.burntout.domain.repository.TableroRepository
import dev.wdona.burntout.presentation.viewmodel.viewmodels.TablerosViewModel
import dev.wdona.burntout.shared.db.DatabaseInit

actual class TablerosViewModelFactory {
    actual fun create(): TablerosViewModel {
        val database = DatabaseInit.getDatabase()

        val dao = TableroDaoImpl(database)
        val api = TableroApiImpl()
        val pendienteDao = OperacionPendienteDaoImpl(database)

        val localDataSource = TableroLocalDataSourceImpl(dao)
        val remoteDataSource = TableroRemoteDataSourceImpl(api)
        val pendienteDataSource = OperacionPendienteLocalDataSourceImpl(pendienteDao)

        val repository = TableroRepositoryImpl(localDataSource, remoteDataSource, pendienteDataSource)

        return getInstance(repository)
    }

    companion object {
        private var instance: TablerosViewModel? = null
        fun getInstance(repository: TableroRepository): TablerosViewModel {
            if (instance == null) {
                instance = TablerosViewModel(repository)
            }
            return instance!!
        }
    }
}
