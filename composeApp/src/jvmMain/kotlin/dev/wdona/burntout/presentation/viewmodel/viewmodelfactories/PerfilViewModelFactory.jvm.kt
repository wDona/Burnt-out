package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import dev.wdona.burntout.data.api.impl.UsuarioApiImpl
import dev.wdona.burntout.data.dao.impl.OperacionPendienteDaoImpl
import dev.wdona.burntout.data.dao.impl.UsuarioDaoImpl
import dev.wdona.burntout.data.datasource.local.impl.OperacionPendienteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.UsuarioLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.UsuarioRemoteDataSourceImpl
import dev.wdona.burntout.data.repository.UsuarioRepositoryImpl
import dev.wdona.burntout.domain.repository.UsuarioRepository
import dev.wdona.burntout.presentation.viewmodel.viewmodels.PerfilViewModel
import dev.wdona.burntout.shared.db.DatabaseActions

actual class MiPerfilViewModelFactory {
    actual fun create(): PerfilViewModel {
        val database = DatabaseActions.getDatabase()

        val dao = UsuarioDaoImpl(database)
        val api = UsuarioApiImpl()
        val pendienteDao = OperacionPendienteDaoImpl(database)

        val localDataSource = UsuarioLocalDataSourceImpl(dao)
        val remoteDataSource = UsuarioRemoteDataSourceImpl(api)
        val pendienteDataSource = OperacionPendienteLocalDataSourceImpl(pendienteDao)

        val repository = UsuarioRepositoryImpl(localDataSource, remoteDataSource, pendienteDataSource)

        return getInstance(repository)
    }

    companion object {
        private var instance: PerfilViewModel? = null
        fun getInstance(repository: UsuarioRepository): PerfilViewModel {
            if (instance == null) {
                instance = PerfilViewModel(repository)
            }
            return instance!!
        }
    }
}
