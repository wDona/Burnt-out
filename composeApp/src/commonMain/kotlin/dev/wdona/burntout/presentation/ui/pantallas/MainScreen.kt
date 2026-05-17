package dev.wdona.burntout.presentation.ui.pantallas

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.CrossfadeTransition
import cafe.adriel.voyager.navigator.tab.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import dev.wdona.burntout.presentation.ui.pantallas.equipo.EquipoScreen
import dev.wdona.burntout.presentation.ui.pantallas.equipo.LeaderboardScreen
import dev.wdona.burntout.presentation.ui.pantallas.formulario.PreguntaScreen
import dev.wdona.burntout.presentation.ui.pantallas.perfil.PerfilScreen
import dev.wdona.burntout.presentation.ui.pantallas.tablero.TablerosScreen
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.*
import dev.wdona.burntout.shared.utils.SettingsManager

class MainScreen(
    private val tareaFactory: TareasViewModelFactory,
    private val equipoFactory: EquipoViewModelFactory,
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
        val tablerosTab = remember { TablerosTab(tableroFactory, tareaFactory, operacionesPendientesFactory) }
        val equipoTab = remember { EquipoTab(equipoFactory, perfilFactory, ajustesFactory) }
        val leaderboardTab = remember { LeaderboardTab(leaderboardFactory, ajustesFactory, equipoFactory, perfilFactory) }
        val perfilTab = remember { PerfilTab(perfilFactory, ajustesFactory) }
        val preguntasTab = remember { PreguntasTab(formularioFactory) }

        val allTabs = remember { listOf<Tab>(tablerosTab, equipoTab, leaderboardTab, perfilTab, preguntasTab) }
        val initialTab = remember {
            val savedKey = SettingsManager.getUltimoTab()
            allTabs.firstOrNull { it.key == savedKey } ?: tablerosTab
        }

        TabNavigator(initialTab) { tabNavigator ->
            LaunchedEffect(tabNavigator.current.key) {
                SettingsManager.setUltimoTab(tabNavigator.current.key)
            }
            Scaffold(
                content = { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues).fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                        Crossfade(targetState = tabNavigator.current) { tab ->
                            tab.Content()
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

private interface PopToRootTab : Tab {
    fun requestPopToRoot()
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
            if (isSelected) {
                (tab as? PopToRootTab)?.requestPopToRoot()
            } else {
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
    val tareaFactory: TareasViewModelFactory,
    val operacionesPendientesFactory: OperacionesPendientesViewModelFactory
) : PopToRootTab {
    override val key = "TablerosTab"

    companion object { val popSignal = MutableStateFlow(0) }

    override fun requestPopToRoot() { popSignal.update { it + 1 } }

    @get:Composable
    override val options: TabOptions
        get() = TabOptions(index = 0u, title = "Tableros", icon = rememberVectorPainter(Icons.Default.Home))

    @Composable
    override fun Content() {
        Navigator(TablerosScreen(factory, tareaFactory, operacionesPendientesFactory)) { navigator ->
            val signal by popSignal.collectAsState()

            LaunchedEffect(signal) { if (signal > 0) navigator.popUntilRoot() }
            CrossfadeTransition(navigator)
        }
    }
}

private class EquipoTab(
    val factory: EquipoViewModelFactory,
    val perfilFactory: MiPerfilViewModelFactory,
    val settingsFactory: AjustesViewModelFactory
) : PopToRootTab {
    override val key = "EquipoTab"

    companion object { val popSignal = MutableStateFlow(0) }

    override fun requestPopToRoot() { popSignal.update { it + 1 } }

    @get:Composable
    override val options: TabOptions
        get() = TabOptions(index = 1u, title = "Equipo", icon = rememberVectorPainter(Icons.Default.Groups))

    @Composable
    override fun Content() {
        Navigator(EquipoScreen(factory, perfilFactory, settingsFactory, idEquipo = null)) { navigator ->
            val signal by popSignal.collectAsState()
            LaunchedEffect(signal) { if (signal > 0) navigator.popUntilRoot() }
            CrossfadeTransition(navigator)
        }
    }
}

private class LeaderboardTab(
    val factory: LeaderboardViewModelFactory,
    val settingsFactory: AjustesViewModelFactory,
    val equipoFactory: EquipoViewModelFactory,
    val perfilFactory: MiPerfilViewModelFactory
) : PopToRootTab {
    override val key = "LeaderboardTab"

    companion object { val popSignal = MutableStateFlow(0) }

    override fun requestPopToRoot() { popSignal.update { it + 1 } }

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
            val signal by popSignal.collectAsState()
            LaunchedEffect(signal) { if (signal > 0) navigator.popUntilRoot() }
            CrossfadeTransition(navigator)
        }
    }
}

private class PerfilTab(
    val factory: MiPerfilViewModelFactory,
    val ajustesFactory: AjustesViewModelFactory
) : PopToRootTab {
    override val key = "PerfilTab"

    companion object { val popSignal = MutableStateFlow(0) }

    override fun requestPopToRoot() { popSignal.update { it + 1 } }

    @get:Composable
    override val options: TabOptions
        get() = TabOptions(index = 3u, title = "Perfil", icon = rememberVectorPainter(Icons.Default.AccountCircle))

    @Composable
    override fun Content() {
        Navigator(PerfilScreen(factory, ajustesFactory)) { navigator ->
            val signal by popSignal.collectAsState()
            LaunchedEffect(signal) { if (signal > 0) navigator.popUntilRoot() }
            CrossfadeTransition(navigator)
        }
    }
}

private class PreguntasTab(
    val factory: FormularioViewModelFactory,
) : PopToRootTab {
    override val key = "PreguntasTab"

    companion object { val popSignal = MutableStateFlow(0) }

    override fun requestPopToRoot() { popSignal.update { it + 1 } }

    @get:Composable
    override val options: TabOptions
        get() = TabOptions(index = 4u, title = "Diario", icon = rememberVectorPainter(Icons.Default.QuestionAnswer))

    @Composable
    override fun Content() {
        Navigator(PreguntaScreen(factory)) { navigator ->
            val signal by popSignal.collectAsState()
            LaunchedEffect(signal) { if (signal > 0) navigator.popUntilRoot() }
            CrossfadeTransition(navigator)
        }
    }
}