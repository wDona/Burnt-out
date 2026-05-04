package dev.wdona.burntout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.CrossfadeTransition
import dev.wdona.burntout.presentation.ui.pantallas.LoginScreen
import dev.wdona.burntout.presentation.ui.pantallas.PreMainScreen
import dev.wdona.burntout.presentation.ui.theme.BurntOutMaterialTheme
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.*
import dev.wdona.burntout.presentation.viewmodel.viewmodels.EstadoSync
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.OperacionesPendientesViewModelFactory
import dev.wdona.burntout.shared.utils.SettingsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    tareaFactory: TareasViewModelFactory,
    equipoViewModelFactory: EquipoViewModelFactory,
    miPerfilViewModelFactory: MiPerfilViewModelFactory,
    tablerosViewModelFactory: TablerosViewModelFactory,
    leaderboardViewModelFactory: LeaderboardViewModelFactory,
    ajustesViewModelFactory: AjustesViewModelFactory,
    formularioViewModelFactory: FormularioViewModelFactory,
    operacionesPendientesViewModelFactory: OperacionesPendientesViewModelFactory,
    loginViewModelFactory: LoginViewModelFactory,
    networkObserver: NetworkObserver
) {
    val isAutenticado by SettingsManager.isAutenticadoFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(isAutenticado) {
        if (!isAutenticado) return@LaunchedEffect
        var previous: Boolean? = null
        networkObserver.isConnected.collect { connected ->
            if (previous == false && connected) {
                operacionesPendientesViewModelFactory.create().sincronizarPorReconexion()
            }
            previous = connected
        }
    }

    BurntOutMaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BurntOutMaterialTheme.getColorScheme().background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (!isAutenticado) {
                    Navigator(
                        LoginScreen(
                            factory = loginViewModelFactory,
                            settingsFactory = ajustesViewModelFactory,
                        )
                    ) { navigator ->
                        CrossfadeTransition(navigator)
                    }
                } else {
                    SyncStatusObserver(operacionesPendientesViewModelFactory, snackbarHostState)
                    Navigator(
                        PreMainScreen(
                            tareaFactory = tareaFactory,
                            equipoFactory = equipoViewModelFactory,
                            perfilFactory = miPerfilViewModelFactory,
                            tableroFactory = tablerosViewModelFactory,
                            leaderboardFactory = leaderboardViewModelFactory,
                            formularioFactory = formularioViewModelFactory,
                            ajustesFactory = ajustesViewModelFactory,
                            operacionesPendientesFactory = operacionesPendientesViewModelFactory,
                            loginFactory = loginViewModelFactory
                        )
                    ) { navigator ->
                        PressBackHandler(enabled = navigator.canPop) {
                            navigator.pop()
                        }
                        CrossfadeTransition(navigator)
                    }
                }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp),
                    snackbar = { data -> Snackbar(data) }
                )
            }
        }
    }
}

@Composable
private fun SyncStatusObserver(
    factory: OperacionesPendientesViewModelFactory,
    snackbarHostState: SnackbarHostState
) {
    val vm = remember { factory.create() }
    val estadoSync by vm.estadoSync.collectAsStateWithLifecycle()

    LaunchedEffect(estadoSync) {
        snackbarHostState.currentSnackbarData?.dismiss()
        when (estadoSync) {
            EstadoSync.SINCRONIZANDO -> snackbarHostState.showSnackbar(
                message = "Sincronizando cambios...",
                duration = SnackbarDuration.Long
            )
            EstadoSync.COMPLETADO -> snackbarHostState.showSnackbar(
                message = "Cambios sincronizados",
                duration = SnackbarDuration.Short
            )
            EstadoSync.COMPLETADO_SIN_CAMBIOS -> snackbarHostState.showSnackbar(
                message = "Todo al día",
                duration = SnackbarDuration.Short
            )
            EstadoSync.COMPLETADO_CON_ERRORES -> snackbarHostState.showSnackbar(
                message = "Algunos cambios no pudieron sincronizarse",
                duration = SnackbarDuration.Long
            )
            EstadoSync.IDLE -> {}
        }
    }
}
