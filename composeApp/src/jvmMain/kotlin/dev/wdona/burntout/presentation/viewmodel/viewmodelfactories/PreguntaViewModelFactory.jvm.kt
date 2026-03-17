package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import dev.wdona.burntout.data.api.impl.PreguntaRespuestaApiImpl
import dev.wdona.burntout.data.dao.PreguntaRespuestaRepository
import dev.wdona.burntout.data.dao.impl.OperacionPendienteDaoImpl
import dev.wdona.burntout.data.dao.impl.PreguntaRespuestaDaoImpl
import dev.wdona.burntout.data.datasource.local.impl.OperacionPendienteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.PreguntaRespuestaLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.PreguntaRespuestaRemoteDataSourceImpl
import dev.wdona.burntout.data.repository.PreguntaRespuestaRepositoryImpl
import dev.wdona.burntout.presentation.viewmodel.viewmodels.FormularioViewModel
import dev.wdona.burntout.shared.db.DatabaseInit

actual class FormularioViewModelFactory {
    actual fun create(): FormularioViewModel {
        val database = DatabaseInit.getDatabase()

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

