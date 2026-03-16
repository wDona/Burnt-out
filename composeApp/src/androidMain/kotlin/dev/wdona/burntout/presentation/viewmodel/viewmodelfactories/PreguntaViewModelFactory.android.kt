package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import android.content.Context
import dev.wdona.burntout.data.api.impl.PreguntaApiImpl
import dev.wdona.burntout.data.dao.PreguntaRepository
import dev.wdona.burntout.data.dao.impl.OperacionPendienteDaoImpl
import dev.wdona.burntout.data.dao.impl.PreguntaDaoImpl
import dev.wdona.burntout.data.datasource.local.impl.OperacionPendienteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.PreguntaLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.PreguntaRemoteDataSourceImpl
import dev.wdona.burntout.data.repository.PreguntaRepositoryImpl
import dev.wdona.burntout.presentation.viewmodel.viewmodels.PreguntaViewModel
import dev.wdona.burntout.shared.db.AppDatabase
import dev.wdona.burntout.shared.db.DatabaseDriverFactory
import java.io.Serializable
import kotlin.jvm.Transient

actual class PreguntaViewModelFactory(@Transient private val context: Context) : Serializable {

    actual fun create(): PreguntaViewModel {
        val driverFactory = DatabaseDriverFactory(context)
        val database = AppDatabase(driverFactory.createDriver())

        val dao = PreguntaDaoImpl(database)
        val api = PreguntaApiImpl()
        val pendienteDao = OperacionPendienteDaoImpl(database)

        val localDataSource = PreguntaLocalDataSourceImpl(dao)
        val remoteDataSource = PreguntaRemoteDataSourceImpl(api)
        val pendienteDataSource = OperacionPendienteLocalDataSourceImpl(pendienteDao)

        val repository = PreguntaRepositoryImpl(localDataSource, remoteDataSource, pendienteDataSource)

        return getInstance(repository)
    }

    companion object {
        private var instance: PreguntaViewModel? = null

        fun getInstance(repository: PreguntaRepository): PreguntaViewModel {
            if (instance == null) {
                instance = PreguntaViewModel(repository)
            }
            return instance!!
        }
    }
}

