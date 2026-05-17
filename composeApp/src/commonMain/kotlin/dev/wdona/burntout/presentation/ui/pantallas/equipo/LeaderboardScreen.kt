package dev.wdona.burntout.presentation.ui.pantallas.equipo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.wdona.burntout.presentation.ui.components.common.FilaTextoPlaceholder
import dev.wdona.burntout.presentation.ui.components.equipo.CrearEquipoDialog
import dev.wdona.burntout.presentation.ui.components.equipo.EquipoCard
import dev.wdona.burntout.presentation.ui.components.equipo.InvitarOrgDialog
import dev.wdona.burntout.presentation.ui.components.equipo.RenombrarEquipoDialog
import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.AjustesViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.LeaderboardViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.EquipoViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.MiPerfilViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.LeaderboardViewModel
import dev.wdona.burntout.shared.utils.SettingsManager

class LeaderboardScreen(
    val factory: LeaderboardViewModelFactory,
    val idOrg: Long,
    val equipoFactory: EquipoViewModelFactory,
    val perfilFactory: MiPerfilViewModelFactory,
    val ajustesFactory: AjustesViewModelFactory,
    val onVolver: (() -> Unit)? = null
) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { factory.create() }
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        val idUsuarioActual by SettingsManager.idUsuarioActualFlow.collectAsState()
        val nombreUsuario = SettingsManager.getNombreUsuario()

        var mostrarCrearEquipoDialog by remember { mutableStateOf(false) }
        var mostrarInvitarDialog by remember { mutableStateOf(false) }

        LaunchedEffect(idOrg) {
            viewModel.cargarLeaderboard(idOrg)
        }

        LaunchedEffect(uiState.createdEquipoId) {
            uiState.createdEquipoId?.let { idEquipo ->
                navigator.push(EquipoScreen(equipoFactory, perfilFactory, ajustesFactory, onVolver = { navigator.pop() }, idEquipo = idEquipo))
                viewModel.resetCreatedEquipoId()
            }
        }

        if (mostrarCrearEquipoDialog) {
            CrearEquipoDialog(
                defaultNombre = "Equipo de $nombreUsuario",
                onDismiss = { mostrarCrearEquipoDialog = false },
                onConfirm = { nombre ->
                    viewModel.crearEquipo(nombre, idOrg, idUsuarioActual)
                    mostrarCrearEquipoDialog = false
                }
            )
        }

        if (mostrarInvitarDialog) {
            InvitarOrgDialog(
                uiState = uiState,
                onGenerar = { rol ->
                    viewModel.generarInvitacion(idUsuarioActual, rol)
                },
                onDismiss = {
                    mostrarInvitarDialog = false
                    viewModel.clearInvitacion()
                }
            )
        }

        LeaderboardContent(
            leaderboardViewModel = viewModel,
            onEquipoClick = { idEquipo ->
                navigator.push(EquipoScreen(equipoFactory, perfilFactory, ajustesFactory, onVolver = { navigator.pop() }, idEquipo = idEquipo))
            },
            onCrearEquipoClick = { mostrarCrearEquipoDialog = true },
            onInvitarClick = { mostrarInvitarDialog = true },
            onVolver = onVolver
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardContent(
    leaderboardViewModel: LeaderboardViewModel,
    onEquipoClick: (Long) -> Unit,
    onCrearEquipoClick: () -> Unit,
    onInvitarClick: () -> Unit,
    onVolver: (() -> Unit)? = null
) {
    val uiState by leaderboardViewModel.uiState.collectAsStateWithLifecycle()
    val listaEquipos = uiState.leaderboard
    val isLoading = uiState.isLoading
    val esInvitado = SettingsManager.isUsuarioInvitado()
    val esAdmin = SettingsManager.isAdminOrOwner()

    var equipoParaRenombrar by remember { mutableStateOf<Equipo?>(null) }

    val titulo = if (isLoading) "" else "Leaderboard"

    if (equipoParaRenombrar != null) {
        RenombrarEquipoDialog(
            nombreActual = equipoParaRenombrar!!.titulo,
            onDismiss = { equipoParaRenombrar = null },
            onConfirm = { nuevoNombre ->
                leaderboardViewModel.renombrarEquipo(equipoParaRenombrar!!, nuevoNombre)
                equipoParaRenombrar = null
            }
        )
    }

    ScaffoldBase(
        titulo = titulo,
        onVolver = onVolver,
        onCrear = if (!esInvitado) { { onCrearEquipoClick() } } else null,
        iconCrear = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir") },
        onFAB = if (!esInvitado && esAdmin) { { onInvitarClick() } } else null,
        textoFAB = "Invitar",
        iconFAB = { Icon(Icons.Default.Share, contentDescription = "Invitar") }
    ) {
        if (isLoading) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val tamanio = if (maxWidth < 600.dp) maxWidth else 300.dp
                Column {
                    (0..3).forEach { _ ->
                        FilaTextoPlaceholder(
                            modifier = Modifier.width(tamanio),
                            paddingTop = 32
                        )
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                items(listaEquipos, key = { it.idEquipo }) { equipo ->
                    EquipoCard(
                        equipo,
                        onClick = { onEquipoClick(equipo.idEquipo) },
                        onRenombrar = if (esAdmin) { { equipoParaRenombrar = equipo } } else null
                    )
                }
            }
        }
    }
}
