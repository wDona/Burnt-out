package dev.wdona.burntout.presentation.ui.pantallas.tarea

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.wdona.burntout.domain.model.TipoEstadoTarea
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.TareasViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.TareasViewModel
import dev.wdona.burntout.shared.domain.Tarea
import dev.wdona.burntout.shared.utils.SettingsManager

class TareaDetalleScreen(private val idTarea: Long, private val idTablero: Long, private val factory: TareasViewModelFactory) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { factory.create() }

        LaunchedEffect(idTarea, idTablero) {
            viewModel.cargarTareaPorId(idTarea, idTablero)
        }

        TareaDetalleContent(
            viewModel,
            onVolver = { navigator.pop() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareaDetalleContent(viewModel: TareasViewModel, onVolver: () -> Unit) {
    val tarea by viewModel.uiState.collectAsStateWithLifecycle()

    var textStateNombreTarea by remember { mutableStateOf("") }
    var textStateDescripcion by remember { mutableStateOf("") }

    var estadoExpanded by remember { mutableStateOf(false) }
    var estadoSelected by remember { mutableStateOf(TipoEstadoTarea.PENDIENTE) }

    LaunchedEffect(tarea) {
        textStateNombreTarea = tarea?.titulo ?: ""
        textStateDescripcion = tarea?.descripcion ?: ""

        for (estado in TipoEstadoTarea.entries) {
            if (estado.string.equals(tarea?.estado, ignoreCase = true)) {
                estadoSelected = estado
                break
            }
        }
    }
    val ejecutarEnvio = {
        if (textStateNombreTarea.isNotBlank()) {
            val tarea = Tarea(
                idTarea = tarea?.idTarea ?: Long.MIN_VALUE,
                titulo = textStateNombreTarea,
                descripcion = textStateDescripcion,
                estado = estadoSelected.string,
                idTableroPerteneciente = tarea?.idTableroPerteneciente ?: Long.MIN_VALUE,
                idUsuarioAsignado = SettingsManager.getIdUsuarioActual(),
                idSubtareas = emptyList()
            )
            viewModel.actualizarTarea(tarea)
            onVolver()
        }
    }

    ScaffoldBase(
        titulo = "Editar Tarea: " + (tarea?.titulo ?: ""),
        onVolver = onVolver,
        onFAB = ejecutarEnvio,
        textoFAB = "Editar Tarea"
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = textStateNombreTarea,
                onValueChange = { textStateNombreTarea = it },
                label = { Text("Título") },
                placeholder = { Text("Hacer la compra...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            // Selector de Estado
            ExposedDropdownMenuBox(
                expanded = estadoExpanded,
                onExpandedChange = { estadoExpanded = !estadoExpanded },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = estadoSelected.string,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = estadoExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = estadoExpanded,
                    onDismissRequest = { estadoExpanded = false }
                ) {
                    TipoEstadoTarea.entries.forEach { estado ->
                        DropdownMenuItem(
                            text = { Text(estado.string) },
                            onClick = {
                                estadoSelected = estado
                                estadoExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = textStateDescripcion,
                onValueChange = { textStateDescripcion = it },
                label = { Text("Descripcion") },
                placeholder = { Text("Detalles de la tarea...") },
                modifier = Modifier
                    .fillMaxHeight(0.4f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { ejecutarEnvio() }
                ),
                singleLine = false
            )
        }
    }
}



