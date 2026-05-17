package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import dev.wdona.burntout.data.api.impl.AjusteApiImpl
import dev.wdona.burntout.data.api.impl.EquipoApiImpl
import dev.wdona.burntout.data.api.impl.UsuarioApiImpl
import dev.wdona.burntout.data.dao.impl.AjusteDaoImpl
import dev.wdona.burntout.data.dao.impl.EquipoDaoImpl
import dev.wdona.burntout.data.dao.impl.OperacionPendienteDaoImpl
import dev.wdona.burntout.data.dao.impl.UsuarioDaoImpl
import dev.wdona.burntout.data.datasource.local.impl.AjusteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.EquipoLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.OperacionPendienteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.UsuarioLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.AjusteRemoteDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.EquipoRemoteDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.UsuarioRemoteDataSourceImpl
import dev.wdona.burntout.data.repository.AjusteRepositoryImpl
import dev.wdona.burntout.data.repository.UsuarioRepositoryImpl
import dev.wdona.burntout.domain.repository.AjusteRepository
import dev.wdona.burntout.domain.repository.UsuarioRepository
import dev.wdona.burntout.notification.NotificacionProgramador
import dev.wdona.burntout.presentation.viewmodel.viewmodels.AjustesViewModel
import dev.wdona.burntout.shared.db.DatabaseActions

actual class AjustesViewModelFactory {
    actual fun create(): AjustesViewModel {
        val database = DatabaseActions.getDatabase()

        val dao = AjusteDaoImpl(database)
        val api = AjusteApiImpl()
        val pendienteDao = OperacionPendienteDaoImpl(database)

        val localDataSource = AjusteLocalDataSourceImpl(dao)
        val remoteDataSource = AjusteRemoteDataSourceImpl(api)
        val pendienteDataSource = OperacionPendienteLocalDataSourceImpl(pendienteDao)

        val equipoDao = EquipoDaoImpl(database)
        val equipoLocal = EquipoLocalDataSourceImpl(equipoDao)
        val equipoApi = EquipoApiImpl()
        val equipoRemote = EquipoRemoteDataSourceImpl(equipoApi)

        val usuarioApi = UsuarioApiImpl()
        val usuarioRemote = UsuarioRemoteDataSourceImpl(usuarioApi)

        val usuarioDao = UsuarioDaoImpl(database)
        val usuarioLocal = UsuarioLocalDataSourceImpl(usuarioDao)
        val usuarioRepository = UsuarioRepositoryImpl(usuarioLocal, usuarioRemote, pendienteDataSource)

        val repository = AjusteRepositoryImpl(
            localDataSource,
            remoteDataSource,
            equipoRemote,
            usuarioRemote,
            equipoLocal,
            pendienteDataSource,
        )

        val notificacionProgramador = NotificacionProgramador()
        val onCancelarNotificaciones: (Long) -> Unit = { _ ->
            notificacionProgramador.cancelarTodasLasNotificaciones()
        }
        val onReprogramarNotificaciones: (Long) -> Unit = { idUsuario ->
            val ahora = System.currentTimeMillis() / 1000
            DatabaseActions.getDatabase()
                .appDatabaseQueries.getTareasConFechaByUsuario(idUsuario)
                .executeAsList()
                .filter { (it.Fecha_Vencimiento ?: 0L) > ahora }
                .forEach { tarea ->
                    val fecha = tarea.Fecha_Vencimiento ?: return@forEach
                    notificacionProgramador.programarNotificaciones(
                        idTarea = tarea.ID_Tarea,
                        titulo = tarea.Titulo,
                        fechaVencimiento = fecha * 1000L,
                        notificacionPersonalizada = tarea.Notificacion_Personalizada?.let { it * 1000L }
                    )
                }
        }

        return getInstance(repository, usuarioRepository, onCancelarNotificaciones, onReprogramarNotificaciones)
    }

    companion object {
        private var instance: AjustesViewModel? = null
        fun getInstance(
            repository: AjusteRepository,
            usuarioRepository: UsuarioRepository,
            onCancelarNotificaciones: (Long) -> Unit = {},
            onReprogramarNotificaciones: (Long) -> Unit = {}
        ): AjustesViewModel {
            if (instance == null) {
                instance = AjustesViewModel(repository, usuarioRepository, onCancelarNotificaciones, onReprogramarNotificaciones)
            }
            return instance!!
        }
    }
}
