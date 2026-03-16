package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import dev.wdona.burntout.data.api.impl.TareaApiImpl
import dev.wdona.burntout.data.dao.TareaRepository
import dev.wdona.burntout.data.dao.impl.OperacionPendienteDaoImpl
import dev.wdona.burntout.data.dao.impl.TareaDaoImpl
import dev.wdona.burntout.data.datasource.local.impl.OperacionPendienteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.TareaLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.TareaRemoteDataSourceImpl
import dev.wdona.burntout.data.repository.TareaRepositoryImpl
import dev.wdona.burntout.presentation.viewmodel.viewmodels.TareasViewModel
import dev.wdona.burntout.shared.db.DatabaseInit

actual class TareasViewModelFactory {
    actual fun create(): TareasViewModel {
        val database = DatabaseInit.getDatabase()

        val dao = TareaDaoImpl(database)
        val api = TareaApiImpl()
        val pendienteDao = OperacionPendienteDaoImpl(database)

        val localDataSource = TareaLocalDataSourceImpl(dao)
        val remoteDataSource = TareaRemoteDataSourceImpl(api)
        val pendienteDataSource = OperacionPendienteLocalDataSourceImpl(pendienteDao)

        val repository = TareaRepositoryImpl(localDataSource, remoteDataSource, pendienteDataSource)

        return getInstance(repository)
    }

    companion object {
        private var instance: TareasViewModel? = null
        fun getInstance(repository: TareaRepository): TareasViewModel {
            if (instance == null) {
                instance = TareasViewModel(repository)
            }
            return instance!!
        }
    }
}