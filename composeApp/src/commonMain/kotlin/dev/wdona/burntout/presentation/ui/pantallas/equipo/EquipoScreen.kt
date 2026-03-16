package dev.wdona.burntout.presentation.ui.pantallas.equipo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.MiEquipoViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.EquipoViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import dev.wdona.burntout.presentation.ui.components.equipo.MiembroCard
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.ui.pantallas.perfil.PerfilScreen
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.AjustesViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.MiPerfilViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.PerfilViewModel
import dev.wdona.burntout.shared.utils.SettingsManager

class EquipoScreen(val factory: MiEquipoViewModelFactory, val perfilFactory: MiPerfilViewModelFactory, val ajustesFactory: AjustesViewModelFactory, val onVolver: (() -> Unit)? = null) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow // Para poder volver o ir a otra

        val viewModel = rememberScreenModel { factory.create() }
        val idEquipo = SettingsManager.getIdEquipoActual()
        val perfilViewModel = rememberScreenModel { perfilFactory.create() }

        LaunchedEffect(idEquipo) {
            viewModel.cargarEquipoPorId(idEquipo)
            viewModel.cargarMiembrosEquipo(idEquipo)
        }

        EquipoContent(
            viewModel,
            onVolver = onVolver,
            onClickUsuario = {
                navigator.push(PerfilScreen(perfilFactory, ajustesFactory, onVolver = { navigator.pop() }))
            },
            perfilViewModel
        )
    }
}

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EquipoContent(viewModel: EquipoViewModel, onVolver: (() -> Unit)? = null, onClickUsuario: (Long) -> Unit, perfilViewModel: PerfilViewModel) {
        val equipo by viewModel.uiStateEquipo.collectAsStateWithLifecycle()
        val miembros by viewModel.listaMiembros.collectAsStateWithLifecycle()

        ScaffoldBase(
            titulo = (equipo?.titulo ?: "Mi equipo (off)") + " (" + (equipo?.puntuacion ?: "0") + "pts)",
            onVolver = onVolver
        ) {
            Column {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 400.dp), // el min size es el tamanio ancho de cada tarjeta
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(miembros) { miembro ->
                        MiembroCard(miembro, onClick = {
                            perfilViewModel.cargarUsuario(miembro.idUsuario)
                            onClickUsuario(miembro.idUsuario)
                        })
                    }
                }
            }
        }
    }
