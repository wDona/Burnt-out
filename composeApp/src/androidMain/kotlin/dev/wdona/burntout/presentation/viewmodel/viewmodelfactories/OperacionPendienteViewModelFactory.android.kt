package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import android.content.Context
import dev.wdona.burntout.data.api.impl.AjusteApiImpl
import dev.wdona.burntout.data.api.impl.EquipoApiImpl
import dev.wdona.burntout.data.api.impl.OrganizacionApiImpl
import dev.wdona.burntout.data.api.impl.PreguntaRespuestaApiImpl
import dev.wdona.burntout.data.api.impl.SubtareaApiImpl
import dev.wdona.burntout.data.api.impl.SyncApiImpl
import dev.wdona.burntout.data.api.impl.TableroApiImpl
import dev.wdona.burntout.data.api.impl.TareaApiImpl
import dev.wdona.burntout.data.api.impl.UsuarioApiImpl
import dev.wdona.burntout.data.dao.impl.AjusteDaoImpl
import dev.wdona.burntout.data.dao.impl.EquipoDaoImpl
import dev.wdona.burntout.data.dao.impl.OperacionPendienteDaoImpl
import dev.wdona.burntout.data.dao.impl.OrganizacionDaoImpl
import dev.wdona.burntout.data.dao.impl.PreguntaRespuestaDaoImpl
import dev.wdona.burntout.data.dao.impl.SubtareaDaoImpl
import dev.wdona.burntout.data.dao.impl.TableroDaoImpl
import dev.wdona.burntout.data.dao.impl.TareaDaoImpl
import dev.wdona.burntout.data.dao.impl.UsuarioDaoImpl
import dev.wdona.burntout.data.datasource.local.impl.AjusteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.EquipoLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.OperacionPendienteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.OrganizacionLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.PreguntaRespuestaLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.SubtareaLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.TableroLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.TareaLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.UsuarioLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.AjusteRemoteDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.EquipoRemoteDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.OrganizacionRemoteDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.PreguntaRespuestaRemoteDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.SubtareaRemoteDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.TableroRemoteDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.TareaRemoteDataSourceImpl
import dev.wdona.burntout.data.datasource.remote.impl.UsuarioRemoteDataSourceImpl
import dev.wdona.burntout.data.repository.AjusteRepositoryImpl
import dev.wdona.burntout.data.repository.EquipoRepositoryImpl
import dev.wdona.burntout.data.repository.OperacionesPendientesRepositoryImpl
import dev.wdona.burntout.data.repository.SyncRepositoryImpl
import dev.wdona.burntout.data.repository.TableroRepositoryImpl
import dev.wdona.burntout.platform.NotificacionProgramador
import dev.wdona.burntout.domain.repository.OperacionesPendientesRepository
import dev.wdona.burntout.domain.repository.SyncRepository
import dev.wdona.burntout.domain.usecase.RefrescarDatosUseCase
import dev.wdona.burntout.domain.usecase.SincronizarPendientesUseCase
import dev.wdona.burntout.presentation.viewmodel.viewmodels.OperacionesPendientesViewModel
import dev.wdona.burntout.shared.db.DatabaseActions
import dev.wdona.burntout.shared.utils.SettingsManager
import java.io.Serializable
import kotlin.jvm.Transient

actual class OperacionesPendientesViewModelFactory(@Transient private val context: Context) : Serializable {
    actual fun create(): OperacionesPendientesViewModel {
        val database = DatabaseActions.getDatabase()

        val pendienteDao = OperacionPendienteDaoImpl(database)
        val pendienteDataSource = OperacionPendienteLocalDataSourceImpl(pendienteDao)
        val pendientesRepository = OperacionesPendientesRepositoryImpl(pendienteDataSource)

        // Remote datasources
        val tareaRemote = TareaRemoteDataSourceImpl(TareaApiImpl())
        val tableroRemote = TableroRemoteDataSourceImpl(TableroApiImpl())
        val equipoRemote = EquipoRemoteDataSourceImpl(EquipoApiImpl())
        val usuarioRemote = UsuarioRemoteDataSourceImpl(UsuarioApiImpl())
        val preguntaRespuestaRemote = PreguntaRespuestaRemoteDataSourceImpl(PreguntaRespuestaApiImpl())
        val ajusteRemote = AjusteRemoteDataSourceImpl(AjusteApiImpl())
        val subtareaRemote = SubtareaRemoteDataSourceImpl(SubtareaApiImpl())
        val organizacionRemote = OrganizacionRemoteDataSourceImpl(OrganizacionApiImpl())

        // Local datasources
        val tareaLocal = TareaLocalDataSourceImpl(TareaDaoImpl(database))
        val subtareaLocal = SubtareaLocalDataSourceImpl(SubtareaDaoImpl(database))
        val tableroLocal = TableroLocalDataSourceImpl(TableroDaoImpl(database))
        val equipoLocal = EquipoLocalDataSourceImpl(EquipoDaoImpl(database))
        val usuarioLocal = UsuarioLocalDataSourceImpl(UsuarioDaoImpl(database))
        val organizacionLocal = OrganizacionLocalDataSourceImpl(OrganizacionDaoImpl(database))
        val preguntaRespuestaLocal = PreguntaRespuestaLocalDataSourceImpl(PreguntaRespuestaDaoImpl(database))
        val ajusteLocal = AjusteLocalDataSourceImpl(AjusteDaoImpl(database))

        val sincronizarPendientes = SincronizarPendientesUseCase(
            pendientesRepository,
            tareaRemote,
            tableroRemote,
            equipoRemote,
            usuarioRemote,
            preguntaRespuestaRemote,
            ajusteRemote,
            subtareaRemote,
            organizacionRemote
        )

        val notificacionProgramador = NotificacionProgramador(context)
        val syncRepository: SyncRepository = SyncRepositoryImpl(
            SyncApiImpl(),
            sincronizarPendientes,
            tareaLocal,
            subtareaLocal,
            tableroLocal,
            equipoLocal,
            usuarioLocal,
            organizacionLocal,
            preguntaRespuestaLocal,
            ajusteLocal,
            onTareasSincronizadas = { tareas ->
                tareas.forEach { tarea ->
                    tarea.fechaVencimiento?.let { fecha ->
                        notificacionProgramador.programarNotificaciones(tarea.idTarea, tarea.titulo, fecha, tarea.notificacionPersonalizada)
                    }
                }
            }
        )

        val tableroRepo = TableroRepositoryImpl(tableroLocal, tableroRemote, pendienteDataSource)
        val equipoRepo = EquipoRepositoryImpl(equipoLocal, equipoRemote, usuarioRemote, pendienteDataSource)
        val ajusteRepo = AjusteRepositoryImpl(ajusteLocal, ajusteRemote, equipoRemote, usuarioRemote, equipoLocal, pendienteDataSource)

        val refrescarDatos = RefrescarDatosUseCase(tableroRepo, equipoRepo, ajusteRepo)

        val verificarUsuarioActivo: suspend () -> Boolean = {
            try {
                val id = SettingsManager.getIdUsuarioActual()
                if (id == Long.MIN_VALUE) true else !usuarioRemote.getUserById(id).isDeleted
            } catch (_: Exception) {
                true
            }
        }

        return getInstance(pendientesRepository, syncRepository, refrescarDatos, verificarUsuarioActivo)
    }

    companion object {
        private var instance: OperacionesPendientesViewModel? = null
        fun getInstance(
            repository: OperacionesPendientesRepository,
            syncRepository: SyncRepository,
            refrescarDatos: RefrescarDatosUseCase,
            verificarUsuarioActivo: suspend () -> Boolean = { true }
        ): OperacionesPendientesViewModel {
            if (instance == null) {
                instance = OperacionesPendientesViewModel(repository, syncRepository, refrescarDatos, verificarUsuarioActivo)
            }
            return instance!!
        }
    }
}
