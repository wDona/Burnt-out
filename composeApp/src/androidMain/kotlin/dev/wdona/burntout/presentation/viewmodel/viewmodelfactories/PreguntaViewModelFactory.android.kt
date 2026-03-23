package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import android.content.Context
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
        
        val usuarioDao = UsuarioDaoImpl(database)
        val usuarioApi = UsuarioApiImpl()
        
        val pendienteDao = OperacionPendienteDaoImpl(database)

        val localDataSource = PreguntaRespuestaLocalDataSourceImpl(dao)
        val remoteDataSource = PreguntaRespuestaRemoteDataSourceImpl(api)
        
        val usuarioLocalDataSource = UsuarioLocalDataSourceImpl(usuarioDao)
        val usuarioRemoteDataSource = UsuarioRemoteDataSourceImpl(usuarioApi)
        
        val pendienteDataSource = OperacionPendienteLocalDataSourceImpl(pendienteDao)

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
