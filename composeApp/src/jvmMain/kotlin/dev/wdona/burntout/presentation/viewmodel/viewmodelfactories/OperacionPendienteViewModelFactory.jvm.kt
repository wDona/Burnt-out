package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import dev.wdona.burntout.data.api.impl.AjusteApiImpl
import dev.wdona.burntout.data.api.impl.EquipoApiImpl
import dev.wdona.burntout.data.api.impl.OrganizacionApiImpl
import dev.wdona.burntout.data.api.impl.PreguntaRespuestaApiImpl
import dev.wdona.burntout.data.api.impl.SubtareaApiImpl
import dev.wdona.burntout.data.api.impl.TableroApiImpl
import dev.wdona.burntout.data.api.impl.TareaApiImpl
import dev.wdona.burntout.data.api.impl.UsuarioApiImpl
import dev.wdona.burntout.data.dao.impl.AjusteDaoImpl
import dev.wdona.burntout.data.dao.impl.EquipoDaoImpl
import dev.wdona.burntout.data.dao.impl.OperacionPendienteDaoImpl
import dev.wdona.burntout.data.dao.impl.TableroDaoImpl
import dev.wdona.burntout.data.datasource.local.impl.AjusteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.EquipoLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.OperacionPendienteLocalDataSourceImpl
import dev.wdona.burntout.data.datasource.local.impl.TableroLocalDataSourceImpl
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
import dev.wdona.burntout.data.repository.TableroRepositoryImpl
import dev.wdona.burntout.domain.repository.OperacionesPendientesRepository
import dev.wdona.burntout.domain.usecase.RefrescarDatosUseCase
import dev.wdona.burntout.domain.usecase.SincronizarPendientesUseCase
import dev.wdona.burntout.presentation.viewmodel.viewmodels.OperacionesPendientesViewModel
import dev.wdona.burntout.shared.db.DatabaseActions

actual class OperacionesPendientesViewModelFactory {
    actual fun create(): OperacionesPendientesViewModel {
        val database = DatabaseActions.getDatabase()

        val pendienteDao = OperacionPendienteDaoImpl(database)
        val pendienteDataSource = OperacionPendienteLocalDataSourceImpl(pendienteDao)
        val pendientesRepository = OperacionesPendientesRepositoryImpl(pendienteDataSource)

        // Remote datasources para sincronizar operaciones pendientes
        val tareaRemote = TareaRemoteDataSourceImpl(TareaApiImpl())
        val tableroRemote = TableroRemoteDataSourceImpl(TableroApiImpl())
        val equipoRemote = EquipoRemoteDataSourceImpl(EquipoApiImpl())
        val usuarioRemote = UsuarioRemoteDataSourceImpl(UsuarioApiImpl())
        val preguntaRespuestaRemote = PreguntaRespuestaRemoteDataSourceImpl(PreguntaRespuestaApiImpl())
        val ajusteRemote = AjusteRemoteDataSourceImpl(AjusteApiImpl())
        val subtareaRemote = SubtareaRemoteDataSourceImpl(SubtareaApiImpl())
        val organizacionRemote = OrganizacionRemoteDataSourceImpl(OrganizacionApiImpl())

        val sincronizarPendientes = SincronizarPendientesUseCase(
            pendientesRepository, tareaRemote, tableroRemote, equipoRemote,
            usuarioRemote, preguntaRespuestaRemote, ajusteRemote, subtareaRemote, organizacionRemote
        )

        // Repos para refrescar datos tras la sincronización
        val tableroLocal = TableroLocalDataSourceImpl(TableroDaoImpl(database))
        val tableroRepo = TableroRepositoryImpl(tableroLocal, tableroRemote, pendienteDataSource)

        val equipoLocal = EquipoLocalDataSourceImpl(EquipoDaoImpl(database))
        val equipoRepo = EquipoRepositoryImpl(equipoLocal, equipoRemote, usuarioRemote, pendienteDataSource)

        val ajusteLocal = AjusteLocalDataSourceImpl(AjusteDaoImpl(database))
        val ajusteRepo = AjusteRepositoryImpl(
            ajusteLocal,
            ajusteRemote,
            equipoRemote,
            usuarioRemote,
            equipoLocal,
            pendienteDataSource
        )

        val refrescarDatos = RefrescarDatosUseCase(tableroRepo, equipoRepo, ajusteRepo)

        return getInstance(pendientesRepository, sincronizarPendientes, refrescarDatos)
    }

    companion object {
        private var instance: OperacionesPendientesViewModel? = null
        fun getInstance(
            repository: OperacionesPendientesRepository,
            sincronizarPendientes: SincronizarPendientesUseCase,
            refrescarDatos: RefrescarDatosUseCase
        ): OperacionesPendientesViewModel {
            if (instance == null) {
                instance = OperacionesPendientesViewModel(repository, sincronizarPendientes, refrescarDatos)
            }
            return instance!!
        }
    }
}