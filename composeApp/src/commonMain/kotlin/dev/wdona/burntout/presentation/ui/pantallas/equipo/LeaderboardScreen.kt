package dev.wdona.burntout.presentation.ui.pantallas.equipo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import dev.wdona.burntout.presentation.ui.components.equipo.EquipoCard
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

        LeaderboardContent(
            leaderboardViewModel = viewModel,
            onEquipoClick = { idEquipo ->
                navigator.push(EquipoScreen(equipoFactory, perfilFactory, ajustesFactory, onVolver = { navigator.pop() }, idEquipo = idEquipo))
            },
            onCrearEquipoClick = {
                mostrarCrearEquipoDialog = true
            },
            onVolver = onVolver
        )
    }
}

@Composable
fun CrearEquipoDialog(defaultNombre: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var nombre by remember { mutableStateOf(defaultNombre) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear nuevo equipo") },
        text = {
            Column {
                Text("Introduce el nombre para tu nuevo equipo:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del equipo") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(nombre) },
                enabled = nombre.isNotBlank()
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardContent(
    leaderboardViewModel: LeaderboardViewModel,
    onEquipoClick: (Long) -> Unit,
    onCrearEquipoClick: () -> Unit,
    onVolver: (() -> Unit)? = null
) {
    val uiState by leaderboardViewModel.uiState.collectAsStateWithLifecycle()
    val listaEquipos = uiState.leaderboard
    val isLoading = uiState.isLoading

    val titulo = if (isLoading) "" else "Leaderboard"

    ScaffoldBase(
        titulo = titulo,
        onVolver = onVolver,
        onFAB = { onCrearEquipoClick() },
        textoFAB = "Crear equipo",
        iconFAB = { Icon(Icons.Default.Add, contentDescription = "Crear equipo") }
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
                        onClick = { onEquipoClick(equipo.idEquipo) }
                    )
                }
            }
        }
    }
}
