package dev.wdona.burntout.presentation.ui.pantallas.equipo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material3.TextButton
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.wdona.burntout.presentation.ui.components.equipo.AnadirUsuarioDialog
import dev.wdona.burntout.shared.domain.Usuario
import dev.wdona.burntout.shared.utils.SettingsManager

class EquipoScreen(val factory: EquipoViewModelFactory, val perfilFactory: MiPerfilViewModelFactory, val ajustesFactory: AjustesViewModelFactory, val onVolver: (() -> Unit)? = null, val idEquipo: Long? = null) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow // Para poder volver o ir a otra

        val viewModel = rememberScreenModel { factory.create() }
        val perfilViewModel = rememberScreenModel { perfilFactory.create() }

        val idEquipoActual by SettingsManager.idEquipoActualFlow.collectAsStateWithLifecycle()
        val targetIdEquipo = idEquipo ?: idEquipoActual

        val esMiEquipo = targetIdEquipo == idEquipoActual && !SettingsManager.isUsuarioInvitado()

        var mostrarAnadirUsuarioDialog by remember { mutableStateOf(false) }
        var miembroAEliminar by remember { mutableStateOf<Usuario?>(null) }
        var mostrarConfirmacion1 by remember { mutableStateOf(false) }
        var mostrarConfirmacion2 by remember { mutableStateOf(false) }
        var textoConfirmacion by remember { mutableStateOf("") }

        LaunchedEffect(targetIdEquipo) {
            viewModel.cargarEquipoPorId(targetIdEquipo)
            viewModel.cargarMiembrosEquipo(targetIdEquipo)
        }

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(uiState.userAddedSuccess) {
            if (uiState.userAddedSuccess) {
                mostrarAnadirUsuarioDialog = false
                viewModel.resetUserAddedSuccess()
            }
        }

        LaunchedEffect(uiState.usuarioEliminadoExitoso) {
            if (uiState.usuarioEliminadoExitoso) {
                miembroAEliminar = null
                mostrarConfirmacion1 = false
                mostrarConfirmacion2 = false
                textoConfirmacion = ""
                viewModel.resetUsuarioEliminadoExitoso()
            }
        }

        if (mostrarAnadirUsuarioDialog) {
            AnadirUsuarioDialog(
                onDismiss = {
                    mostrarAnadirUsuarioDialog = false
                    viewModel.resetUserAddedSuccess()
                },
                onAddUsuario = { input ->
                    viewModel.anadirUsuarioAlEquipoPorNombre(targetIdEquipo, input)
                },
                onClearError = { viewModel.resetError() },
                uiState = uiState
            )
        }

        if (mostrarConfirmacion1 && miembroAEliminar != null) {
            AlertDialog(
                onDismissRequest = {
                    mostrarConfirmacion1 = false
                    miembroAEliminar = null
                },
                title = { Text("¿Eliminar usuario?") },
                text = { Text("¿Seguro que lo quieres eliminar? Esta acción es irrecuperable. Toda su información se perderá.") },
                confirmButton = {
                    TextButton(onClick = {
                        mostrarConfirmacion1 = false
                        mostrarConfirmacion2 = true
                    }) { Text("Continuar", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = {
                        mostrarConfirmacion1 = false
                        miembroAEliminar = null
                    }) { Text("Cancelar") }
                }
            )
        }

        if (mostrarConfirmacion2 && miembroAEliminar != null) {
            val username = miembroAEliminar!!.username
            val textoEsperado = "eliminar $username"
            AlertDialog(
                onDismissRequest = {
                    mostrarConfirmacion2 = false
                    miembroAEliminar = null
                    textoConfirmacion = ""
                },
                title = { Text("Confirmar eliminación") },
                text = {
                    Column {
                        Text("Escribe \"eliminar $username\" para confirmar:")
                        OutlinedTextField(
                            value = textoConfirmacion,
                            onValueChange = { textoConfirmacion = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (textoConfirmacion == textoEsperado) {
                                    miembroAEliminar?.let { viewModel.eliminarUsuario(it.idUsuario) }
                                    textoConfirmacion = ""
                                }
                            })
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        enabled = textoConfirmacion == textoEsperado,
                        onClick = {
                            miembroAEliminar?.let { viewModel.eliminarUsuario(it.idUsuario) }
                            textoConfirmacion = ""
                        }
                    ) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = {
                        mostrarConfirmacion2 = false
                        miembroAEliminar = null
                        textoConfirmacion = ""
                    }) { Text("Cancelar") }
                }
            )
        }

        val esAdminOrOwner = SettingsManager.isAdminOrOwner()
        val puedeEliminarUsuarios = esAdminOrOwner && !SettingsManager.isUsuarioInvitado()

        EquipoContent(
            viewModel,
            esMiEquipo = esMiEquipo,
            esAdminOrOwner = esAdminOrOwner,
            onVolver = onVolver,
            onClickAddUsuario = {
                mostrarAnadirUsuarioDialog = true
            },
            onClickUsuario = {
                navigator.push(PerfilScreen(perfilFactory, ajustesFactory, onVolver = { navigator.pop() }, idUsuario = it))
            },
            onEliminarUsuario = if (puedeEliminarUsuarios) { miembro ->
                miembroAEliminar = miembro
                mostrarConfirmacion1 = true
            } else null,
            perfilViewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipoContent(viewModel: EquipoViewModel, esMiEquipo: Boolean, esAdminOrOwner: Boolean = false, onVolver: (() -> Unit)? = null, onClickAddUsuario: () -> Unit, onClickUsuario: (Long) -> Unit, onEliminarUsuario: ((Usuario) -> Unit)? = null, perfilViewModel: PerfilViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val titulo = if (uiState.isLoading) "" else (uiState.equipo?.titulo ?: "Offline")
    val subtitulo = if (uiState.isLoading || uiState.equipo == null) "" else "Nivel ${uiState.equipo?.nivel} • ${uiState.equipo?.puntosRestantes}/${uiState.equipo?.costoSiguienteNivel} exp"

    ScaffoldBase(
        titulo = titulo,
        subtitle = subtitulo,
        onVolver = onVolver,
        onFAB = if (esMiEquipo) { { onClickAddUsuario() } } else null,
        textoFAB = if (esMiEquipo) "" else null,
        iconFAB = if (esMiEquipo) { { Icon(Icons.Default.Add, contentDescription = "Añadir usuario") } } else null,
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
                            },
                            esAdminOrOwner = esAdminOrOwner,
                            onCambiarRol = if (esAdminOrOwner) { nuevoRol ->
                                viewModel.cambiarRol(miembro.idUsuario, nuevoRol)
                            } else null,
                            onEliminar = if (onEliminarUsuario != null) {
                                { onEliminarUsuario(miembro) }
                            } else null
                        )
                    }
                }
            }
        }
    }
}
