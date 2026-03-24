package dev.wdona.burntout

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import dev.wdona.burntout.presentation.ui.pantallas.MainScreen
import dev.wdona.burntout.presentation.ui.pantallas.PreMainScreen
import dev.wdona.burntout.presentation.ui.theme.BurntOutMaterialTheme
import dev.wdona.burntout.presentation.ui.theme.DarkColorScheme
import dev.wdona.burntout.presentation.ui.theme.LightColorScheme
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    tareaFactory: TareasViewModelFactory,
    miEquipoViewModelFactory: MiEquipoViewModelFactory,
    miPerfilViewModelFactory: MiPerfilViewModelFactory,
    tablerosViewModelFactory: TablerosViewModelFactory,
    leaderboardViewModelFactory: LeaderboardViewModelFactory,
    ajustesViewModelFactory: AjustesViewModelFactory,
    formularioViewModelFactory: FormularioViewModelFactory
) {
    BurntOutMaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BurntOutMaterialTheme.getColorScheme().background
        ) {
            Navigator(
                PreMainScreen(
                    tareaFactory = tareaFactory,
                    equipoFactory = miEquipoViewModelFactory,
                    perfilFactory = miPerfilViewModelFactory,
                    tableroFactory = tablerosViewModelFactory,
                    leaderboardFactory = leaderboardViewModelFactory,
                    formularioFactory = formularioViewModelFactory,
                    ajustesFactory = ajustesViewModelFactory
                )
            ) { navigator ->
                PressBackHandler(enabled = navigator.canPop) {
                    navigator.pop()
                }
                SlideTransition(navigator)
            }
        }
    }
}
