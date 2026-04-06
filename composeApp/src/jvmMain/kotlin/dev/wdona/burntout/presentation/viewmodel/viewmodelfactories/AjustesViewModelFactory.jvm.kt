package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import dev.wdona.burntout.data.api.impl.AjusteApiImpl
import dev.wdona.burntout.data.api.impl.EquipoApiImpl
import dev.wdona.burntout.data.api.impl.UsuarioApiImpl
import dev.wdona.burntout.data.dao.impl.AjusteDaoImpl
import dev.wdona.burntout.data.dao.impl.EquipoDaoImpl
import dev.wdona.burntout.data.dao.impl.OperacionPendienteDaoImpl
import dev.wdona.burntout.data.datasource.local.impl.AjusteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.EquipoLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.OperacionPendienteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.AjusteRemoteDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.EquipoRemoteDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.UsuarioRemoteDataSourceImpl
import dev.wdona.burntout.data.repository.AjusteRepositoryImpl
import dev.wdona.burntout.domain.repository.AjusteRepository
import dev.wdona.burntout.presentation.viewmodel.viewmodels.AjustesViewModel
import dev.wdona.burntout.shared.db.DatabaseActions

actual class AjustesViewModelFactory {
    actual fun create(): AjustesViewModel {
        val database = DatabaseActions.getDatabase()

        val dao = AjusteDaoImpl(database)
        val api = AjusteApiImpl()
        val pendienteDao = OperacionPendienteDaoImpl(database)

        val localDataSource = AjusteLocalDataSourceImpl(dao)
        val remoteDataSource = AjusteRemoteDataSourceImpl(api)
        val pendienteDataSource = OperacionPendienteLocalDataSourceImpl(pendienteDao)

        val equipoDao = EquipoDaoImpl(database)
        val equipoLocal = EquipoLocalDataSourceImpl(equipoDao)
        val equipoApi = EquipoApiImpl()
        val equipoRemote = EquipoRemoteDataSourceImpl(equipoApi)

        val usuarioApi = UsuarioApiImpl()
        val usuarioRemote = UsuarioRemoteDataSourceImpl(usuarioApi)

        val repository = AjusteRepositoryImpl(
            localDataSource,
            remoteDataSource,
            equipoRemote,
            usuarioRemote,
            equipoLocal,
            pendienteDataSource,
        )

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
