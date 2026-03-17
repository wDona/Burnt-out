package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import android.content.Context
import dev.wdona.burntout.data.api.impl.PreguntaRespuestaApiImpl
import dev.wdona.burntout.data.dao.PreguntaRespuestaRepository
import dev.wdona.burntout.data.dao.impl.OperacionPendienteDaoImpl
import dev.wdona.burntout.data.dao.impl.PreguntaRespuestaDaoImpl
import dev.wdona.burntout.data.datasource.local.impl.OperacionPendienteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.PreguntaRespuestaLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.PreguntaRespuestaRemoteDataSourceImpl
import dev.wdona.burntout.data.repository.PreguntaRespuestaRepositoryImpl
import dev.wdona.burntout.presentation.viewmodel.viewmodels.FormularioViewModel
import dev.wdona.burntout.shared.db.AppDatabase
import dev.wdona.burntout.shared.db.DatabaseDriverFactory
import java.io.Serializable
import kotlin.jvm.Transient

actual class FormularioViewModelFactory(@Transient private val context: Context) : Serializable {

    actual fun create(): FormularioViewModel {
        val driverFactory = DatabaseDriverFactory(context)
        val database = AppDatabase(driverFactory.createDriver())

        val dao = PreguntaRespuestaDaoImpl(database)
        val api = PreguntaRespuestaApiImpl()
        val pendienteDao = OperacionPendienteDaoImpl(database)

        val localDataSource = PreguntaRespuestaLocalDataSourceImpl(dao)
        val remoteDataSource = PreguntaRespuestaRemoteDataSourceImpl(api)
        val pendienteDataSource = OperacionPendienteLocalDataSourceImpl(pendienteDao)

        val repository = PreguntaRespuestaRepositoryImpl(localDataSource, remoteDataSource, pendienteDataSource)

        return getInstance(repository)
    }

    companion object {
        private var instance: FormularioViewModel? = null

        fun getInstance(repository: PreguntaRespuestaRepository): FormularioViewModel {
            if (instance == null) {
                instance = FormularioViewModel(repository)
            }
            return instance!!
        }
    }
}

