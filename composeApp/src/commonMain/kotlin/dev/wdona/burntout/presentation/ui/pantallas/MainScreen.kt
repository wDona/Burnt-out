package dev.wdona.burntout.presentation.ui.pantallas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.*
import cafe.adriel.voyager.transitions.SlideTransition
import dev.wdona.burntout.presentation.ui.pantallas.equipo.EquipoScreen
import dev.wdona.burntout.presentation.ui.pantallas.equipo.LeaderboardScreen
import dev.wdona.burntout.presentation.ui.pantallas.formulario.PreguntaScreen
import dev.wdona.burntout.presentation.ui.pantallas.formulario.PreguntasInicialesScreen
import dev.wdona.burntout.presentation.ui.pantallas.perfil.PerfilScreen
import dev.wdona.burntout.presentation.ui.pantallas.tablero.TablerosScreen
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.*
import dev.wdona.burntout.shared.utils.SettingsManager

class MainScreen(
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
        val tablerosTab = remember { TablerosTab(tableroFactory, tareaFactory) }
        val equipoTab = remember { EquipoTab(equipoFactory, perfilFactory, ajustesFactory) }
        val leaderboardTab = remember { LeaderboardTab(leaderboardFactory, ajustesFactory, equipoFactory, perfilFactory) }
        val perfilTab = remember { PerfilTab(perfilFactory, ajustesFactory) }
        val preguntasTab = remember { PreguntasTab(formularioFactory) }

        TabNavigator(tablerosTab) { tabNavigator ->
            Scaffold(
                content = { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        key(tabNavigator.current.key) {
                            tabNavigator.current.Content()
                        }
                    }
                },
                bottomBar = {
                    NavigationBar {
                        TabNavigationItem(tablerosTab, tabNavigator)
                        TabNavigationItem(equipoTab, tabNavigator)
                        TabNavigationItem(leaderboardTab, tabNavigator)
                        TabNavigationItem(perfilTab, tabNavigator)
                        TabNavigationItem(preguntasTab, tabNavigator)
                    }
                }
            )
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(
    tab: Tab,
    tabNavigator: TabNavigator
) {
    val isSelected = tabNavigator.current.key == tab.key

    NavigationBarItem(
        selected = isSelected,
        onClick = {
            if (!isSelected) {
                tabNavigator.current = tab
            }
        },
        label = { Text(tab.options.title) },
        icon = {
            val icon = tab.options.icon ?: rememberVectorPainter(Icons.Default.Home)
            Icon(painter = icon, contentDescription = tab.options.title)
        }
    )
}

private class TablerosTab(
    val factory: TablerosViewModelFactory,
    val tareaFactory: TareasViewModelFactory
) : Tab {
    override val key = "TablerosTab"
    @get:Composable
    override val options: TabOptions
        get() = TabOptions(index = 0u, title = "Tableros", icon = rememberVectorPainter(Icons.Default.Home))

    @Composable
    override fun Content() {
        Navigator(TablerosScreen(factory, tareaFactory)) { navigator ->
            SlideTransition(navigator)
        }
    }
}

private class EquipoTab(
    val factory: MiEquipoViewModelFactory,
    val perfilFactory: MiPerfilViewModelFactory,
    val settingsFactory: AjustesViewModelFactory
) : Tab {
    override val key = "EquipoTab"
    @get:Composable
    override val options: TabOptions
        get() = TabOptions(index = 1u, title = "Equipo", icon = rememberVectorPainter(Icons.Default.Groups))

    @Composable
    override fun Content() {
        Navigator(EquipoScreen(factory, perfilFactory, settingsFactory)) { navigator ->
            SlideTransition(navigator)
        }
    }
}

private class LeaderboardTab(
    val factory: LeaderboardViewModelFactory,
    val settingsFactory: AjustesViewModelFactory,
    val equipoFactory: MiEquipoViewModelFactory,
    val perfilFactory: MiPerfilViewModelFactory
) : Tab {
    override val key = "LeaderboardTab"
    @get:Composable
    override val options: TabOptions
        get() = TabOptions(index = 2u, title = "Ranking", icon = rememberVectorPainter(Icons.Default.EmojiEvents))

    @Composable
    override fun Content() {
        Navigator(LeaderboardScreen(
            factory, SettingsManager.getIdOrganizacionActual(),
            equipoFactory = equipoFactory,
            perfilFactory = perfilFactory,
            ajustesFactory = settingsFactory
        )) { navigator ->
            SlideTransition(navigator)
        }
    }
}

private class PerfilTab(
    val factory: MiPerfilViewModelFactory,
    val ajustesFactory: AjustesViewModelFactory
) : Tab {
    override val key = "PerfilTab"
    @get:Composable
    override val options: TabOptions
        get() = TabOptions(index = 3u, title = "Perfil", icon = rememberVectorPainter(Icons.Default.AccountCircle))

    @Composable
    override fun Content() {
        Navigator(PerfilScreen(factory, ajustesFactory)) { navigator ->
            SlideTransition(navigator)
        }
    }
}

private class PreguntasTab(
    val factory: FormularioViewModelFactory,
) : Tab {
    override val key = "PreguntasTab"
    @get:Composable
    override val options: TabOptions
        get() = TabOptions(index = 4u, title = "Diario", icon = rememberVectorPainter(Icons.Default.QuestionAnswer))

    @Composable
    override fun Content() {
        Navigator(PreguntaScreen(factory)) { navigator ->
            SlideTransition(navigator)
        }
    }
}