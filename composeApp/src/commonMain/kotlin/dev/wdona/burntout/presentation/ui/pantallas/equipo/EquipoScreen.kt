package dev.wdona.burntout.presentation.ui.pantallas.equipo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.AjustesViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.MiEquipoViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.MiPerfilViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.EquipoViewModel
import dev.wdona.burntout.presentation.viewmodel.viewmodels.PerfilViewModel
import dev.wdona.burntout.presentation.ui.components.equipo.MiembroCard
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.ui.pantallas.perfil.PerfilScreen
import dev.wdona.burntout.shared.utils.SettingsManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import dev.wdona.burntout.presentation.ui.components.common.FilaTextoPlaceholder
import androidx.compose.runtime.collectAsState

class EquipoScreen(val factory: MiEquipoViewModelFactory, val perfilFactory: MiPerfilViewModelFactory, val ajustesFactory: AjustesViewModelFactory, val onVolver: (() -> Unit)? = null, val idEquipo: Long) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow // Para poder volver o ir a otra

        val ajustesViewModel = rememberScreenModel { ajustesFactory.create() }

        val viewModel = rememberScreenModel { factory.create() }
        val perfilViewModel = rememberScreenModel { perfilFactory.create() }

        LaunchedEffect(idEquipo) {
            viewModel.cargarEquipoPorId(idEquipo)
            viewModel.cargarMiembrosEquipo(idEquipo)
        }

        EquipoContent(
            viewModel,
            onVolver = onVolver,
            onClickUsuario = {
                navigator.push(PerfilScreen(perfilFactory, ajustesFactory, onVolver = { navigator.pop() }, idUsuario = it))
            },
            perfilViewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipoContent(viewModel: EquipoViewModel, onVolver: (() -> Unit)? = null, onClickUsuario: (Long) -> Unit, perfilViewModel: PerfilViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val titulo = if (uiState.isLoading) "" else (uiState.equipo?.titulo ?: "Mi equipo (off)")
    val subtitulo = "" + (uiState.equipo?.puntuacion ?: "0") + "pts"

    ScaffoldBase(
        titulo = titulo,
        subtitle = subtitulo,
        onVolver = onVolver
    ) {
         Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
                Column {
                    (0..3).forEach { _ ->
                        FilaTextoPlaceholder(
                            paddingEnd = 48,
                            paddingTop = 32
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 400.dp), // el min size es el tamanio ancho de cada tarjeta
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(uiState.miembros) { miembro ->
                        MiembroCard(miembro, onClick = {
                            perfilViewModel.cargarUsuario(miembro.idUsuario)
                            onClickUsuario(miembro.idUsuario)
                        })
                    }
                }
            }
        }
    }
}
