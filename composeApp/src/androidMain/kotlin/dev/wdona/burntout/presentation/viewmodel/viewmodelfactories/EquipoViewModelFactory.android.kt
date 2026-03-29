package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import android.content.Context
import dev.wdona.burntout.data.api.impl.EquipoApiImpl
import dev.wdona.burntout.data.api.impl.UsuarioApiImpl
import dev.wdona.burntout.data.dao.impl.EquipoDaoImpl
import dev.wdona.burntout.data.dao.impl.OperacionPendienteDaoImpl
import dev.wdona.burntout.data.dao.impl.UsuarioDaoImpl
import dev.wdona.burntout.data.datasource.local.impl.EquipoLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.OperacionPendienteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.UsuarioLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.EquipoRemoteDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.UsuarioRemoteDataSourceImpl
import dev.wdona.burntout.data.repository.EquipoRepositoryImpl
import dev.wdona.burntout.data.repository.UsuarioRepositoryImpl
import dev.wdona.burntout.domain.repository.EquipoRepository
import dev.wdona.burntout.domain.repository.UsuarioRepository
import dev.wdona.burntout.domain.usecase.AddUsuarioAlEquipoUseCase
import dev.wdona.burntout.domain.usecase.CargarMiembrosEquipo
import dev.wdona.burntout.domain.usecase.GetUsuarioByUsernameUseCase
import dev.wdona.burntout.shared.db.DatabaseDriverFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.EquipoViewModel
import dev.wdona.burntout.shared.db.AppDatabase
import java.io.Serializable
import kotlin.jvm.Transient

actual class EquipoViewModelFactory(@Transient private val context: Context) : Serializable {
    actual fun create(): EquipoViewModel {
        val driverFactory = DatabaseDriverFactory(context)
        val database = AppDatabase(driverFactory.createDriver())

        val dao = EquipoDaoImpl(database)
        val api = EquipoApiImpl()
        val pendienteDao = OperacionPendienteDaoImpl(database)

        val localDataSource = EquipoLocalDataSourceImpl(dao)
        val remoteDataSource = EquipoRemoteDataSourceImpl(api)
        val pendienteDataSource = OperacionPendienteLocalDataSourceImpl(pendienteDao)

        val repository = EquipoRepositoryImpl(localDataSource, remoteDataSource, pendienteDataSource)

        val usuarioDao = UsuarioDaoImpl(database)
        val usuarioLocalDataSource = UsuarioLocalDataSourceImpl(usuarioDao)
        val usuarioApi = UsuarioApiImpl()
        val usuarioRemoteDataSource = UsuarioRemoteDataSourceImpl(usuarioApi)
        val usuarioRepository =
            UsuarioRepositoryImpl(
                usuarioLocalDataSource,
                usuarioRemoteDataSource,
                pendienteDataSource)

        return getInstance(repository, usuarioRepository)
    }

    companion object {
        private var instance: EquipoViewModel? = null

        fun getInstance(repository: EquipoRepository, usuarioRepository: UsuarioRepository): EquipoViewModel {
            if (instance == null) {
                instance = EquipoViewModel(
                    repository,
                    CargarMiembrosEquipo(usuarioRepository),
                    AddUsuarioAlEquipoUseCase(repository),
                    GetUsuarioByUsernameUseCase(usuarioRepository)
                )
            }
            return instance!!
        }
    }
}
