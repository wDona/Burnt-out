package dev.wdona.burntout.presentation.ui.pantallas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import dev.wdona.burntout.presentation.ui.pantallas.formulario.PreguntasInicialesScreen
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.AjustesViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.FormularioViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.LeaderboardViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.MiEquipoViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.MiPerfilViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.TablerosViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.TareasViewModelFactory

class PreMainScreen(
    private val tareaFactory: TareasViewModelFactory,
    private val equipoFactory: MiEquipoViewModelFactory,
    private val perfilFactory: MiPerfilViewModelFactory,
    private val tableroFactory: TablerosViewModelFactory,
    private val leaderboardFactory: LeaderboardViewModelFactory,
    private val ajustesFactory: AjustesViewModelFactory,
    private val formularioFactory: FormularioViewModelFactory
) : Screen {


    @Composable
    override fun Content() {
        val settingsViewModel = rememberScreenModel { ajustesFactory.create() }
        val uiState by settingsViewModel.ajustesUiState.collectAsStateWithLifecycle()

        if (!uiState.primerCuestionarioHecho) {
            Scaffold { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    Navigator(PreguntasInicialesScreen(
                        formularioFactory,
                        nPreguntas = 22,
                        tareaFactory = tareaFactory,
                        equipoFactory = equipoFactory,
                        perfilFactory = perfilFactory,
                        tableroFactory = tableroFactory,
                        ajustesFactory = ajustesFactory,
                        leaderboardFactory = leaderboardFactory
                    )) { navigator ->
                        SlideTransition(navigator)
                    }
                }
            }
        } else if (!uiState.hoyHecho) {
            Scaffold { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    Navigator(PreguntasInicialesScreen(
                        formularioFactory,
                        nPreguntas = 3,
                        tareaFactory = tareaFactory,
                        equipoFactory = equipoFactory,
                        perfilFactory = perfilFactory,
                        tableroFactory = tableroFactory,
                        ajustesFactory = ajustesFactory,
                        leaderboardFactory = leaderboardFactory
                    )) { navigator ->
                        SlideTransition(navigator)
                    }
                }
            }
        } else {
            Scaffold { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    Navigator(MainScreen(
                        formularioFactory = formularioFactory,
                        tareaFactory = tareaFactory,
                        equipoFactory = equipoFactory,
                        perfilFactory = perfilFactory,
                        tableroFactory = tableroFactory,
                        ajustesFactory = ajustesFactory,
                        leaderboardFactory = leaderboardFactory
                    )) { navigator ->
                        SlideTransition(navigator)
                    }
                }
            }
        }
    }
}
