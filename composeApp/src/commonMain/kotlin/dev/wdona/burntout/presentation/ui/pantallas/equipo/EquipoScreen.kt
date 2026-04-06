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
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.EquipoViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.MiPerfilViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.EquipoViewModel
import dev.wdona.burntout.presentation.viewmodel.viewmodels.PerfilViewModel
import dev.wdona.burntout.presentation.ui.components.equipo.MiembroCard
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.ui.pantallas.perfil.PerfilScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import dev.wdona.burntout.presentation.ui.components.common.FilaTextoPlaceholder
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.wdona.burntout.shared.utils.SettingsManager

class EquipoScreen(val factory: EquipoViewModelFactory, val perfilFactory: MiPerfilViewModelFactory, val ajustesFactory: AjustesViewModelFactory, val onVolver: (() -> Unit)? = null, val idEquipo: Long? = null) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow // Para poder volver o ir a otra

        val viewModel = rememberScreenModel { factory.create() }
        val perfilViewModel = rememberScreenModel { perfilFactory.create() }

        val idEquipoActual by SettingsManager.idEquipoActualFlow.collectAsState()
        val targetIdEquipo = idEquipo ?: idEquipoActual

        val esMiEquipo = targetIdEquipo == idEquipoActual

        var mostrarAnadirUsuarioDialog by remember { mutableStateOf(false) }

        LaunchedEffect(targetIdEquipo) {
            viewModel.cargarEquipoPorId(targetIdEquipo)
            viewModel.cargarMiembrosEquipo(targetIdEquipo)
        }
        
        if (mostrarAnadirUsuarioDialog) {
            AnadirUsuarioDialog(
                onDismiss = { mostrarAnadirUsuarioDialog = false },
                onAddUsuario = { input ->
                    val idLong = input.toLongOrNull()
                    if (idLong != null) {
                        viewModel.addUsuarioAlEquipo(targetIdEquipo, idLong)
                        println("Se ha añadido el usuario con id $idLong al equipo $targetIdEquipo")
                    } else {
                        viewModel.anadirUsuarioAlEquipoPorNombre(targetIdEquipo, input)
                        println("No se ha podido añadir el usuario")
                    }
                    mostrarAnadirUsuarioDialog = false
                }
            )
        }

        EquipoContent(
            viewModel,
            esMiEquipo = esMiEquipo,
            onVolver = onVolver,
            onClickAddUsuario = {
                mostrarAnadirUsuarioDialog = true
            },
            onClickUsuario = {
                navigator.push(PerfilScreen(perfilFactory, ajustesFactory, onVolver = { navigator.pop() }, idUsuario = it))
            },
            perfilViewModel
        )
    }
}

@Composable
fun AnadirUsuarioDialog(onDismiss: () -> Unit, onAddUsuario: (String) -> Unit) {
    var input by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir usuario al equipo") },
        text = {
            Column {
                Text("Introduce el ID o el nombre de usuario de la persona que quieres añadir:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("ID o Nombre de usuario") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAddUsuario(input) },
                enabled = input.isNotBlank()
            ) {
                Text("Añadir")
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
fun EquipoContent(viewModel: EquipoViewModel, esMiEquipo: Boolean, onVolver: (() -> Unit)? = null, onClickAddUsuario: () -> Unit, onClickUsuario: (Long) -> Unit, perfilViewModel: PerfilViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val titulo = if (uiState.isLoading) "" else (uiState.equipo?.titulo ?: "Mi equipo (off)")
    val subtitulo = if (uiState.isLoading) "" else "" + (uiState.equipo?.puntuacion ?: "0") + "pts"

    ScaffoldBase(
        titulo = titulo,
        subtitle = subtitulo,
        onVolver = onVolver,
        onFAB = if (esMiEquipo) { { onClickAddUsuario() } } else null,
        textoFAB = if (esMiEquipo) "Añadir usuario" else null,
        iconFAB = if (esMiEquipo) { { Icon(Icons.Default.Add, contentDescription = "Añadir usuario") } } else null,
        onSalirEquipo = if (esMiEquipo) {
            {
                val idUsuario = SettingsManager.getIdUsuarioActual()
                if (idUsuario != Long.MIN_VALUE) {
                    viewModel.salirDelEquipo(
                        uiState.equipo!!.idEquipo,
                        idUsuario
                    )


                    onVolver?.invoke()
                }
            }
        } else null
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
                    columns = GridCells.Adaptive(minSize = 400.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(uiState.miembros) { miembro ->
                        MiembroCard(
                            miembro,
                            onClick = {
                                perfilViewModel.cargarUsuario(miembro.idUsuario)
                                onClickUsuario(miembro.idUsuario)
                            }
                        )
                    }
                }
            }
        }
    }
}
