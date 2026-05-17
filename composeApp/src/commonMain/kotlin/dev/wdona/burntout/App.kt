package dev.wdona.burntout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.CrossfadeTransition
import dev.wdona.burntout.platform.NetworkObserver
import dev.wdona.burntout.platform.PressBackHandler
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
    val wasConnected = rememberSaveable { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(isAutenticado) {
        if (!isAutenticado) return@LaunchedEffect
        networkObserver.isConnected.collect { connected ->
            if (wasConnected.value == false && connected) {
                operacionesPendientesViewModelFactory.create().sincronizarPorReconexion()
            }
            wasConnected.value = connected
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
                        .align(Alignment.BottomCenter)
                        .padding(top = 24.dp, start = 24.dp, bottom = 72.dp, end = 24.dp)
                        .wrapContentWidth(Alignment.Start),
                    snackbar = { data ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.inverseSurface,
                            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                        ) {
                            Text(
                                text = data.visuals.message,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
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
            EstadoSync.SINCRONIZANDO -> {}
//                snackbarHostState.showSnackbar(
//                message = "Sincronizando cambios...",
//                duration = SnackbarDuration.Indefinite
//            )
            EstadoSync.COMPLETADO -> {}
            EstadoSync.COMPLETADO_SIN_CAMBIOS -> {}
            EstadoSync.CON_ERRORES -> snackbarHostState.showSnackbar(
                message = "Error sincronizando",
                duration = SnackbarDuration.Long
            )
            EstadoSync.IDLE -> {}
            EstadoSync.CON_ERRORES_RECONECTAR -> snackbarHostState.showSnackbar(
                message = "Error sincronizando, revisa tu conexión a internet",
                duration = SnackbarDuration.Long
            )
            EstadoSync.RATE_LIMITED -> snackbarHostState.showSnackbar(
                message = "¡Vas demasiado rápido!",
                duration = SnackbarDuration.Short
            )
        }

        if (estadoSync != EstadoSync.IDLE && estadoSync != EstadoSync.SINCRONIZANDO) {
            vm.resetEstadoSync()
        }
    }
}
