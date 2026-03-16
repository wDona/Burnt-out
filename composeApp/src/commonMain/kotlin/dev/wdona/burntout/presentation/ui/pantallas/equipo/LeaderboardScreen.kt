package dev.wdona.burntout.presentation.ui.pantallas.equipo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.wdona.burntout.presentation.ui.components.equipo.EquipoCard
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.AjustesViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.LeaderboardViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.MiEquipoViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.MiPerfilViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.LeaderboardViewModel

class LeaderboardScreen(
    val factory: LeaderboardViewModelFactory,
    val idOrg: Long,
    val equipoFactory: MiEquipoViewModelFactory,
    val perfilFactory: MiPerfilViewModelFactory,
    val ajustesFactory: AjustesViewModelFactory,
    val onVolver: (() -> Unit)? = null
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { factory.create() }

        LaunchedEffect(idOrg) {
            viewModel.cargarLeaderboard(idOrg)
        }

        LeaderboardContent(
            leaderboardViewModel = viewModel,
            onEquipoClick = { idEquipo ->
                // TODO: Pasar idEquipo a EquipoScreen para cargar uno concreto
                navigator.push(EquipoScreen(equipoFactory, perfilFactory, ajustesFactory, onVolver = { navigator.pop() }))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardContent(
    leaderboardViewModel: LeaderboardViewModel,
    onEquipoClick: (Long) -> Unit,
    onVolver: (() -> Unit)? = null
) {
    val listaEquipos by leaderboardViewModel.leaderboard.collectAsStateWithLifecycle()
    
    ScaffoldBase(
        titulo = "Leaderboard",
        onVolver = onVolver
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 300.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(listaEquipos, key = { it.idEquipo }) { equipo ->
                EquipoCard(
                    equipo, 
                    onClick = { onEquipoClick(equipo.idEquipo) }
                )
            }
        }
    }
}
