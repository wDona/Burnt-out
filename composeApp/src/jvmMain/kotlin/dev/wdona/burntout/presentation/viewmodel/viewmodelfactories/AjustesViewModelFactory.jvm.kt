package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import dev.wdona.burntout.data.api.impl.AjusteApiImpl
import dev.wdona.burntout.data.dao.impl.AjusteDaoImpl
import dev.wdona.burntout.data.dao.impl.OperacionPendienteDaoImpl
import dev.wdona.burntout.data.datasource.local.impl.AjusteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.OperacionPendienteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.AjusteRemoteDataSourceImpl
import dev.wdona.burntout.data.repository.AjusteRepositoryImpl
import dev.wdona.burntout.domain.repository.AjusteRepository
import dev.wdona.burntout.presentation.viewmodel.viewmodels.AjustesViewModel
import dev.wdona.burntout.shared.db.DatabaseInit

actual class AjustesViewModelFactory {
    actual fun create(): AjustesViewModel {
        val database = DatabaseInit.getDatabase()

        val dao = AjusteDaoImpl(database)
        val api = AjusteApiImpl()
        val pendienteDao = OperacionPendienteDaoImpl(database)

        val localDataSource = AjusteLocalDataSourceImpl(dao)
        val remoteDataSource = AjusteRemoteDataSourceImpl(api)
        val pendienteDataSource = OperacionPendienteLocalDataSourceImpl(pendienteDao)

        val repository = AjusteRepositoryImpl(localDataSource, remoteDataSource, pendienteDataSource)

        return getInstance(repository)
    }

    companion object {
        private var instance: AjustesViewModel? = null
        fun getInstance(repository: AjusteRepository): AjustesViewModel {
            if (instance == null) {
                instance = AjustesViewModel(repository)
            }
            return instance!!
        }
    }
}
