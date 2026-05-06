package dev.wdona.burntout.presentation.ui.pantallas.tablero

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.wdona.burntout.presentation.ui.components.tablero.CardTablero
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.OperacionesPendientesViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.TablerosViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.TareasViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.TablerosViewModel
import dev.wdona.burntout.shared.utils.SettingsManager

class TablerosScreen(
    private val tableroFactory: TablerosViewModelFactory,
    private val tareasViewModelFactory: TareasViewModelFactory,
    private val operacionesPendientesViewModelFactory: OperacionesPendientesViewModelFactory
) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val tablerosViewModel: TablerosViewModel = rememberScreenModel { tableroFactory.create() }
        val idOrg = SettingsManager.getIdOrganizacionActual()

        val idEquipo = SettingsManager.getIdEquipoActual()
        LaunchedEffect(idOrg, idEquipo) {
            tablerosViewModel.cargarTableros(idOrg, idEquipo)
        }

        MenuTableros(
            tablerosViewModel = tablerosViewModel,
            idOrg = idOrg,
            onIrACrearTablero = { 
                navigator.push(MenuCrearTableroScreen(tableroFactory)) 
            },
            onVerTablero = { idTablero, nombreTablero ->
                navigator.push(
                    DetalleTableroScreen(
                        idTablero = idTablero,
                        nombreTablero = nombreTablero,
                        tareasViewModelFactory = tareasViewModelFactory,
                        operacionesPendientesViewModelFactory = operacionesPendientesViewModelFactory
                    )
                )
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MenuTableros(
        tablerosViewModel: TablerosViewModel,
        idOrg: Long,
        onIrACrearTablero: () -> Unit,
        onVerTablero: (String, String) -> Unit
    ) {
        val listaTableros by tablerosViewModel.listaTableros.collectAsStateWithLifecycle()

        ScaffoldBase(
            titulo = "Tableros",
            onFAB = onIrACrearTablero,
            textoFAB = ""
        ) {
            if (listaTableros.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        "No hay tableros aun",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(listaTableros) { tablero ->
                        CardTablero(
                            tablero,
                            onClick = {
                                onVerTablero(tablero.idTablero, tablero.titulo)
                            },
                            onDelete = {
                                tablerosViewModel.eliminarTablero(tablero.idTablero, idOrg)
                            },
                            onRename = { nuevoNombre ->
                                tablerosViewModel.actualizarTablero(tablero.copy(titulo = nuevoNombre))
                            }
                        )
                    }
                }
            }
        }
    }
}
