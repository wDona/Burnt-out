package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import android.content.Context
import dev.wdona.burntout.data.api.impl.EquipoApiImpl
import dev.wdona.burntout.data.dao.impl.EquipoDaoImpl
import dev.wdona.burntout.data.dao.impl.OperacionPendienteDaoImpl
import dev.wdona.burntout.data.dao.impl.UsuarioDaoImpl
import dev.wdona.burntout.data.datasource.local.impl.EquipoLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.OperacionPendienteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.UsuarioLocalDataSourceImpl
import dev.wdona.burntout.data.api.impl.UsuarioApiImpl
import dev.wdona.burntout.data.datasource.remote.impl.EquipoRemoteDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.UsuarioRemoteDataSourceImpl
import dev.wdona.burntout.data.repository.EquipoRepositoryImpl
import dev.wdona.burntout.domain.repository.EquipoRepository
import dev.wdona.burntout.presentation.viewmodel.viewmodels.LeaderboardViewModel
import dev.wdona.burntout.shared.db.DatabaseDriverFactory
import dev.wdona.burntout.shared.db.AppDatabase
import java.io.Serializable
import kotlin.jvm.Transient
actual class LeaderboardViewModelFactory(@Transient private val context: Context) : Serializable {
    actual fun create(): LeaderboardViewModel {
        val driverFactory = DatabaseDriverFactory(context)
        val database = AppDatabase(driverFactory.createDriver())

        val dao = EquipoDaoImpl(database)
        val api = EquipoApiImpl()
        val pendienteDao = OperacionPendienteDaoImpl(database)
        val usuarioDao = UsuarioDaoImpl(database)

        val localDataSource = EquipoLocalDataSourceImpl(dao)
        val remoteDataSource = EquipoRemoteDataSourceImpl(api)
        val pendienteDataSource = OperacionPendienteLocalDataSourceImpl(pendienteDao)
        val usuarioLocalDataSource = UsuarioLocalDataSourceImpl(usuarioDao)
        
        val usuarioApi = UsuarioApiImpl()
        val usuarioRemoteDataSource = UsuarioRemoteDataSourceImpl(usuarioApi)

        val repository = EquipoRepositoryImpl(
            localDataSource,
            remoteDataSource,
            usuarioRemoteDataSource,
            pendienteDataSource
        )

        return getInstance(repository)
    }

    companion object {
        private var instance: LeaderboardViewModel? = null
        fun getInstance(repository: EquipoRepository): LeaderboardViewModel {
            if (instance == null) {
                instance = LeaderboardViewModel(repository)
            }
            return instance!!
        }
    }
}
