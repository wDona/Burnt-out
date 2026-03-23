package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import dev.wdona.burntout.data.api.impl.PreguntaRespuestaApiImpl
import dev.wdona.burntout.data.api.impl.UsuarioApiImpl
import dev.wdona.burntout.data.dao.PreguntaRespuestaRepository
import dev.wdona.burntout.data.dao.impl.OperacionPendienteDaoImpl
import dev.wdona.burntout.data.dao.impl.PreguntaRespuestaDaoImpl
import dev.wdona.burntout.data.dao.impl.UsuarioDaoImpl
import dev.wdona.burntout.data.datasource.local.impl.OperacionPendienteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.PreguntaRespuestaLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.UsuarioLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.PreguntaRespuestaRemoteDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.UsuarioRemoteDataSourceImpl
import dev.wdona.burntout.data.repository.PreguntaRespuestaRepositoryImpl
import dev.wdona.burntout.data.repository.UsuarioRepositoryImpl
import dev.wdona.burntout.domain.repository.UsuarioRepository
import dev.wdona.burntout.domain.usecase.CalcularRiesgoBurnout
import dev.wdona.burntout.presentation.viewmodel.viewmodels.FormularioViewModel
import dev.wdona.burntout.shared.db.DatabaseInit

actual class FormularioViewModelFactory {
    actual fun create(): FormularioViewModel {
        val database = DatabaseInit.getDatabase()

        val dao = PreguntaRespuestaDaoImpl(database)
        val api = PreguntaRespuestaApiImpl()
        val pendienteDao = OperacionPendienteDaoImpl(database)
        
        val usuarioDao = UsuarioDaoImpl(database)
        val usuarioApi = UsuarioApiImpl()

        val localDataSource = PreguntaRespuestaLocalDataSourceImpl(dao)
        val remoteDataSource = PreguntaRespuestaRemoteDataSourceImpl(api)
        val pendienteDataSource = OperacionPendienteLocalDataSourceImpl(pendienteDao)
        
        val usuarioLocalDataSource = UsuarioLocalDataSourceImpl(usuarioDao)
        val usuarioRemoteDataSource = UsuarioRemoteDataSourceImpl(usuarioApi)

        val repository = PreguntaRespuestaRepositoryImpl(localDataSource, remoteDataSource, pendienteDataSource)
        val usuarioRepository = UsuarioRepositoryImpl(usuarioLocalDataSource, usuarioRemoteDataSource, pendienteDataSource)
        
        val calcularRiesgoBurnout = CalcularRiesgoBurnout()

        return getInstance(repository, usuarioRepository, calcularRiesgoBurnout)
    }

    companion object {
        private var instance: FormularioViewModel? = null

        fun getInstance(
            repository: PreguntaRespuestaRepository,
            usuarioRepository: UsuarioRepository,
            calcularRiesgoBurnout: CalcularRiesgoBurnout
        ): FormularioViewModel {
            if (instance == null) {
                instance = FormularioViewModel(repository, usuarioRepository, calcularRiesgoBurnout)
            }
            return instance!!
        }
    }
}
