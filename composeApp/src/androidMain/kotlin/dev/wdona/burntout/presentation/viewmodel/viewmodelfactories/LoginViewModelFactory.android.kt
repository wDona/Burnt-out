package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import android.content.Context
import dev.wdona.burntout.data.api.impl.UsuarioApiImpl
import dev.wdona.burntout.data.dao.impl.OperacionPendienteDaoImpl
import dev.wdona.burntout.data.dao.impl.UsuarioDaoImpl
import dev.wdona.burntout.data.datasource.local.impl.OperacionPendienteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.UsuarioLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.UsuarioRemoteDataSourceImpl
import dev.wdona.burntout.data.repository.UsuarioRepositoryImpl
import dev.wdona.burntout.domain.repository.UsuarioRepository
import dev.wdona.burntout.presentation.viewmodel.viewmodels.LoginViewModel
import dev.wdona.burntout.shared.db.AppDatabase
import dev.wdona.burntout.shared.db.DatabaseDriverFactory
import java.io.Serializable
import kotlin.jvm.Transient

actual class LoginViewModelFactory(@Transient private val context: Context) : Serializable {
    actual fun create(): LoginViewModel {
        val driverFactory = DatabaseDriverFactory(context)
        val database = AppDatabase(driverFactory.createDriver())

        val usuarioDao = UsuarioDaoImpl(database)
        val usuarioApi = UsuarioApiImpl()
        val pendienteDao = OperacionPendienteDaoImpl(database)

        val localUsuarioDataSource = UsuarioLocalDataSourceImpl(usuarioDao)
        val remoteUsuarioDataSource = UsuarioRemoteDataSourceImpl(usuarioApi)
        val pendienteDataSource = OperacionPendienteLocalDataSourceImpl(pendienteDao)

        val usuarioRepository = UsuarioRepositoryImpl(
            localUsuarioDataSource,
            remoteUsuarioDataSource,
            pendienteDataSource
        )

        return getInstance(usuarioRepository)
    }

    companion object {
        private var instance: LoginViewModel? = null

        fun getInstance(usuarioRepository: UsuarioRepository): LoginViewModel {
            if (instance == null) {
                instance = LoginViewModel(usuarioRepository)
            }
            return instance!!
        }
    }
}
