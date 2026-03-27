package dev.wdona.burntout.presentation.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.SlideTransition
import dev.wdona.burntout.presentation.ui.pantallas.formulario.PreguntasInicialesScreen
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.AjustesViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.FormularioViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.LeaderboardViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.MiEquipoViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.MiPerfilViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.OperacionesPendientesViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.TablerosViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.TareasViewModelFactory
import dev.wdona.burntout.shared.utils.SettingsManager

class PreMainScreen(
    private val tareaFactory: TareasViewModelFactory,
    private val equipoFactory: MiEquipoViewModelFactory,
    private val perfilFactory: MiPerfilViewModelFactory,
    private val tableroFactory: TablerosViewModelFactory,
    private val leaderboardFactory: LeaderboardViewModelFactory,
    private val ajustesFactory: AjustesViewModelFactory,
    private val formularioFactory: FormularioViewModelFactory,
    private val operacionesPendientesFactory: OperacionesPendientesViewModelFactory
) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val settingsViewModel = rememberScreenModel { ajustesFactory.create() }
        val uiState by settingsViewModel.ajustesUiState.collectAsStateWithLifecycle()

        val syncViewModel = rememberScreenModel { operacionesPendientesFactory.create() }

        LaunchedEffect(Unit) {
            syncViewModel.sincronizarAlIniciar()
        }

        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            )

            LaunchedEffect(uiState.primerCuestionarioHecho, uiState.hoyHecho) {
                if (!uiState.primerCuestionarioHecho) {
                    navigator.replace(
                        PreguntasInicialesScreen(
                            formularioFactory,
                            nPreguntas = 22,
                            tareaFactory = tareaFactory,
                            equipoFactory = equipoFactory,
                            perfilFactory = perfilFactory,
                            tableroFactory = tableroFactory,
                            ajustesFactory = ajustesFactory,
                            leaderboardFactory = leaderboardFactory
                        )
                    )
                } else if (!uiState.hoyHecho) {
                    navigator.replace(
                        PreguntasInicialesScreen(
                            formularioFactory,
                            nPreguntas = 3,
                            tareaFactory = tareaFactory,
                            equipoFactory = equipoFactory,
                            perfilFactory = perfilFactory,
                            tableroFactory = tableroFactory,
                            ajustesFactory = ajustesFactory,
                            leaderboardFactory = leaderboardFactory
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
                            leaderboardFactory = leaderboardFactory
                        )
                    )
                }
            }
        }
    }
}
