package dev.wdona.burntout.presentation.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.wdona.burntout.presentation.ui.pantallas.formulario.PreguntasInicialesScreen
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.AjustesViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.FormularioViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.LeaderboardViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.LoginViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.EquipoViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.MiPerfilViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.OperacionesPendientesViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.TablerosViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.TareasViewModelFactory
import dev.wdona.burntout.shared.utils.SettingsManager

class PreMainScreen(
    private val tareaFactory: TareasViewModelFactory,
    private val equipoFactory: EquipoViewModelFactory,
    private val perfilFactory: MiPerfilViewModelFactory,
    private val tableroFactory: TablerosViewModelFactory,
    private val leaderboardFactory: LeaderboardViewModelFactory,
    private val ajustesFactory: AjustesViewModelFactory,
    private val formularioFactory: FormularioViewModelFactory,
    private val operacionesPendientesFactory: OperacionesPendientesViewModelFactory,
    private val loginFactory: LoginViewModelFactory
) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val syncViewModel = rememberScreenModel { operacionesPendientesFactory.create() }

        LaunchedEffect(Unit) {
            syncViewModel.sincronizarAlIniciar()

            val primerCuestionarioHechoSync = SettingsManager.getPrimerCuestionarioHecho()
            val hoyHechoSync = SettingsManager.esCuestionarioHoyHecho()
            val idEquipoSync = SettingsManager.getIdEquipoActual()

            if (!primerCuestionarioHechoSync) {
                navigator.replace(
                    PreguntasInicialesScreen(
                        formularioFactory,
                        nPreguntas = 22,
                        tareaFactory = tareaFactory,
                        equipoFactory = equipoFactory,
                        perfilFactory = perfilFactory,
                        tableroFactory = tableroFactory,
                        ajustesFactory = ajustesFactory,
                        leaderboardFactory = leaderboardFactory,
                        operacionesPendientesFactory = operacionesPendientesFactory,
                        idEquipo = idEquipoSync
                    )
                )
            } else if (!hoyHechoSync) {
                navigator.replace(
                    PreguntasInicialesScreen(
                        formularioFactory,
                        nPreguntas = 3,
                        tareaFactory = tareaFactory,
                        equipoFactory = equipoFactory,
                        perfilFactory = perfilFactory,
                        tableroFactory = tableroFactory,
                        ajustesFactory = ajustesFactory,
                        leaderboardFactory = leaderboardFactory,
                        operacionesPendientesFactory = operacionesPendientesFactory,
                        idEquipo = idEquipoSync
                    )
                )
            } else {
                navigator.replace(
                    MainScreen(
                        formularioFactory = formularioFactory,
                        tareaFactory = tareaFactory,
                        equipoFactory = equipoFactory,
                        perfilFactory = perfilFactory,
                        tableroFactory = tableroFactory,
                        ajustesFactory = ajustesFactory,
                        leaderboardFactory = leaderboardFactory,
                        operacionesPendientesFactory = operacionesPendientesFactory,
                    )
                )
            }
        }
    }
}
