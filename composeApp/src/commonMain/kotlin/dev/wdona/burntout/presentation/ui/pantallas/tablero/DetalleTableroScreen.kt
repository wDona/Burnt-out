package dev.wdona.burntout.presentation.ui.pantallas.tablero

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.wdona.burntout.presentation.ui.components.tarea.CardTarea
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.ui.pantallas.tarea.MenuCrearTareaScreen
import dev.wdona.burntout.presentation.ui.pantallas.tarea.TareaDetalleScreen
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.OperacionesPendientesViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.TablerosViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.TareasViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.TareasViewModel
import dev.wdona.burntout.shared.utils.SettingsManager

class DetalleTableroScreen(
    val idTablero: String,
    val nombreTablero: String,
    val tareasViewModelFactory: TareasViewModelFactory,
    val operacionesPendientesViewModelFactory: OperacionesPendientesViewModelFactory,
    val tableroFactory: TablerosViewModelFactory
) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val tareaViewModel = rememberScreenModel { tareasViewModelFactory.create() }
        val tablerosViewModel = rememberScreenModel { tableroFactory.create() }
        val syncViewModel = remember { operacionesPendientesViewModelFactory.create() }
        val syncTick by syncViewModel.syncTick.collectAsState()

        LaunchedEffect(Unit) {
            tareaViewModel.cargarTareas(idTablero)
            tareaViewModel.cargarMiembrosEquipo(SettingsManager.getIdEquipoActual())
            syncViewModel.sincronizarPorReconexion()
        }

        LaunchedEffect(syncTick) {
            if (syncTick > 0L) {
                tareaViewModel.cargarTareas(idTablero)
                if (!tablerosViewModel.existeTableroLocal(idTablero)) {
                    navigator.pop()
                }
            }
        }

        DetalleTableroContent(
            tareasViewModel = tareaViewModel,
            nombreTablero = nombreTablero,
            idTablero = idTablero,
            onVolver = { navigator.pop() },
            onIrACrearTarea = {
                navigator.push(
                    MenuCrearTareaScreen(
                        factory = tareasViewModelFactory,
                        idTablero = idTablero,
                    )
                )
            },
            onIrATarea = { idTarea, idTablero ->
                navigator.push(TareaDetalleScreen(idTarea, idTablero, tareasViewModelFactory))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetalleTableroContent(
    tareasViewModel: TareasViewModel,
    nombreTablero: String,
    idTablero: String,
    onVolver: () -> Unit,
    onIrACrearTarea: (String) -> Unit,
    onIrATarea: (String, String) -> Unit,
) {
    val listaTareas by tareasViewModel.listaTareas.collectAsStateWithLifecycle()
    val miembros by tareasViewModel.miembros.collectAsState()

    ScaffoldBase(
        titulo = nombreTablero,
        onVolver = onVolver,
        onFAB = { onIrACrearTarea(idTablero) },
        textoFAB = "Crear tarea"
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (listaTareas.isEmpty()) {
                Text(
                    text = "No hay tareas en este tablero",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            } else {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(listaTareas) { tarea ->
                        val nombreAsignado = when {
                            tarea.idUsuarioAsignado == Long.MIN_VALUE -> "Invitado"
                            else -> miembros.firstOrNull { it.idUsuario == tarea.idUsuarioAsignado }?.nombre
                                ?: tarea.idUsuarioAsignado.toString()
                        }
                        CardTarea(
                            tarea = tarea,
                            nombreAsignado = nombreAsignado,
                            onClick = { onIrATarea(tarea.idTarea, idTablero) },
                            onDelete = { tareasViewModel.eliminarTarea(tarea.idTarea, idTablero) },
                            onCompletar = { tareasViewModel.completarTarea(tarea, SettingsManager.getIdEquipoActual()) }
                        )
                    }
                }
            }
        }
    }
}
