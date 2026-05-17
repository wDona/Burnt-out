package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import android.content.Context
import dev.wdona.burntout.data.api.impl.EquipoApiImpl
import dev.wdona.burntout.data.api.impl.SubtareaApiImpl
import dev.wdona.burntout.data.api.impl.TareaApiImpl
import dev.wdona.burntout.data.api.impl.UsuarioApiImpl
import dev.wdona.burntout.data.dao.impl.EquipoDaoImpl
import dev.wdona.burntout.data.dao.impl.OperacionPendienteDaoImpl
import dev.wdona.burntout.data.dao.impl.SubtareaDaoImpl
import dev.wdona.burntout.data.dao.impl.TareaDaoImpl
import dev.wdona.burntout.data.dao.impl.UsuarioDaoImpl
import dev.wdona.burntout.data.datasource.local.impl.EquipoLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.OperacionPendienteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.SubtareaLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.TareaLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.UsuarioLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.EquipoRemoteDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.SubtareaRemoteDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.TareaRemoteDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.UsuarioRemoteDataSourceImpl
import dev.wdona.burntout.data.repository.EquipoRepositoryImpl
import dev.wdona.burntout.data.repository.SubtareaRepositoryImpl
import dev.wdona.burntout.data.repository.TareaRepositoryImpl
import dev.wdona.burntout.data.repository.UsuarioRepositoryImpl
import dev.wdona.burntout.domain.usecase.CompletarTareaUseCase
import dev.wdona.burntout.platform.NotificacionProgramador
import dev.wdona.burntout.shared.db.DatabaseDriverFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.TareasViewModel
import dev.wdona.burntout.shared.db.AppDatabase
import java.io.Serializable
import kotlin.jvm.Transient

actual class TareasViewModelFactory(@Transient private val context: Context) : Serializable {

    actual fun create(): TareasViewModel {
        val driverFactory = DatabaseDriverFactory(context)
        val database = AppDatabase(driverFactory.createDriver())

        val dao = TareaDaoImpl(database)
        val api = TareaApiImpl()
        val pendienteDao = OperacionPendienteDaoImpl(database)
        val usuarioDao = UsuarioDaoImpl(database)
        val subtareaDao = SubtareaDaoImpl(database)

        val localDataSource = TareaLocalDataSourceImpl(dao)
        val remoteDataSource = TareaRemoteDataSourceImpl(api)
        val pendienteDataSource = OperacionPendienteLocalDataSourceImpl(pendienteDao)
        val usuarioLocalDataSource = UsuarioLocalDataSourceImpl(usuarioDao)
        val usuarioRemoteDataSource = UsuarioRemoteDataSourceImpl(UsuarioApiImpl())
        val subtareaLocalDataSource = SubtareaLocalDataSourceImpl(subtareaDao)
        val subtareaRemoteDataSource = SubtareaRemoteDataSourceImpl(SubtareaApiImpl())

        val repository = TareaRepositoryImpl(localDataSource, remoteDataSource, pendienteDataSource)
        val usuarioRepository = UsuarioRepositoryImpl(usuarioLocalDataSource, usuarioRemoteDataSource, pendienteDataSource)
        val subtareaRepository = SubtareaRepositoryImpl(subtareaLocalDataSource, subtareaRemoteDataSource, pendienteDataSource)

        val equipoDao = EquipoDaoImpl(database)
        val equipoLocalDataSource = EquipoLocalDataSourceImpl(equipoDao)
        val equipoRemoteDataSource = EquipoRemoteDataSourceImpl(EquipoApiImpl())
        val equipoRepository = EquipoRepositoryImpl(equipoLocalDataSource, equipoRemoteDataSource, usuarioRemoteDataSource, pendienteDataSource)

        val notificacionProgramador = NotificacionProgramador(context)
        val completarTareaUseCase = CompletarTareaUseCase(repository, equipoRepository)

        return TareasViewModel(repository, usuarioRepository, subtareaRepository, notificacionProgramador, completarTareaUseCase)
    }
}
