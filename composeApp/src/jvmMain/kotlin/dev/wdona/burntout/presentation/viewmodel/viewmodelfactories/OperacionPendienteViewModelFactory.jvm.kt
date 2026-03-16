package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import dev.wdona.burntout.data.dao.impl.OperacionPendienteDaoImpl
import dev.wdona.burntout.data.datasource.local.impl.OperacionPendienteLocalDataSourceImpl
import dev.wdona.burntout.data.repository.OperacionesPendientesRepositoryImpl
import dev.wdona.burntout.domain.repository.OperacionesPendientesRepository
import dev.wdona.burntout.shared.db.DatabaseDriverFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.OperacionesPendientesViewModel
import dev.wdona.burntout.shared.db.AppDatabase

actual class OperacionesPendientesViewModelFactory {
    actual fun create(): OperacionesPendientesViewModel {
        val driverFactory = DatabaseDriverFactory()
        val database = AppDatabase(driverFactory.createDriver())

        val pendienteDao = OperacionPendienteDaoImpl(database)
        val pendienteDataSource = OperacionPendienteLocalDataSourceImpl(pendienteDao)

        val repository = OperacionesPendientesRepositoryImpl(pendienteDataSource)

        return getInstance(repository)
    }

    companion object {
        private var instance: OperacionesPendientesViewModel? = null
        fun getInstance(repository: OperacionesPendientesRepository): OperacionesPendientesViewModel {
            if (instance == null) {
                instance = OperacionesPendientesViewModel(repository)
            }
            return instance!!
        }
    }
}
