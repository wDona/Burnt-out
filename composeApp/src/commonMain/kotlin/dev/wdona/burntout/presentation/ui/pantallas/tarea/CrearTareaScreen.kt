package dev.wdona.burntout.presentation.ui.pantallas.tarea

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.wdona.burntout.domain.model.TipoEstadoTarea
import dev.wdona.burntout.presentation.ui.components.common.formatearFecha
import dev.wdona.burntout.presentation.ui.components.tarea.DialogConfirmacionBurnout
import dev.wdona.burntout.presentation.ui.components.tarea.SelectorUsuarioConBurnout
import dev.wdona.burntout.presentation.ui.components.tarea.nivelRequiereDialog
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.TareasViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.TareasViewModel
import dev.wdona.burntout.shared.domain.Tarea
import dev.wdona.burntout.shared.domain.Usuario
import dev.wdona.burntout.shared.utils.SettingsManager

class MenuCrearTareaScreen(val factory: TareasViewModelFactory, val idTablero: Long) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: TareasViewModel = rememberScreenModel { factory.create() }

        MenuCrearTareaContent(
            idTablero = idTablero,
            tareasViewModel = viewModel,
            onVolver = { navigator.pop() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuCrearTareaContent(idTablero: Long, tareasViewModel: TareasViewModel, onVolver: () -> Unit) {
    var textStateNombreTarea by remember { mutableStateOf("") }
    var textStateDescripcion by remember { mutableStateOf("") }

    var estadoExpanded by remember { mutableStateOf(false) }
    var estadoSelected by remember { mutableStateOf(TipoEstadoTarea.PENDIENTE) }

    val miembros by tareasViewModel.miembros.collectAsState()
    var usuarioSeleccionado by remember { mutableStateOf<Usuario?>(null) }
    var mostrarDialogBurnout by remember { mutableStateOf(false) }

    var mostrarDatePicker by remember { mutableStateOf(false) }
    var fechaVencimiento by remember { mutableStateOf<Long?>(null) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(Unit) {
        tareasViewModel.cargarMiembrosEquipo(SettingsManager.getIdEquipoActual())
    }

    LaunchedEffect(miembros) {
        if (usuarioSeleccionado == null && miembros.isNotEmpty()) {
            val idActual = SettingsManager.getIdUsuarioActual()
            usuarioSeleccionado = miembros.firstOrNull { it.idUsuario == idActual } ?: miembros.first()
        }
    }

    if (mostrarDatePicker) {
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    fechaVencimiento = datePickerState.selectedDateMillis
                    mostrarDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    fun guardarTarea() {
        if (textStateNombreTarea.isNotBlank()) {
            val nuevaTarea = Tarea(
                idTarea = 0,
                titulo = textStateNombreTarea,
                descripcion = textStateDescripcion,
                estado = estadoSelected.string,
                idTableroPerteneciente = idTablero,
                idUsuarioAsignado = usuarioSeleccionado?.idUsuario ?: SettingsManager.getIdUsuarioActual(),
                idSubtareas = emptyList(),
                fechaVencimiento = fechaVencimiento
            )
            tareasViewModel.crearTarea(nuevaTarea)
            textStateNombreTarea = ""
            textStateDescripcion = ""
            onVolver()
        }
    }

    val ejecutarEnvio = {
        if (textStateNombreTarea.isNotBlank()) {
            if (nivelRequiereDialog(usuarioSeleccionado?.riesgoBurnout)) {
                mostrarDialogBurnout = true
            } else {
                guardarTarea()
            }
        }
    }

    if (mostrarDialogBurnout) {
        DialogConfirmacionBurnout(
            onConfirm = {
                mostrarDialogBurnout = false
                guardarTarea()
            },
            onDismiss = { mostrarDialogBurnout = false }
        )
    }

    ScaffoldBase(
        titulo = "Nueva Tarea",
        onVolver = onVolver,
        onFAB = ejecutarEnvio,
        textoFAB = "Crear Tarea"
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
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
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = estadoExpanded,
                    onDismissRequest = { estadoExpanded = false }
                ) {
                    TipoEstadoTarea.entries.forEach { estado ->
                        val icon = when (estado) {
                            TipoEstadoTarea.PENDIENTE -> Icons.Default.RadioButtonUnchecked
                            TipoEstadoTarea.EN_PROCESO -> Icons.Default.MoreHoriz
                            TipoEstadoTarea.COMPLETADA -> Icons.Default.CheckCircle
                        }
                        DropdownMenuItem(
                            text = { Text(estado.string) },
                            onClick = { estadoSelected = estado; estadoExpanded = false },
                            leadingIcon = {
                                Icon(icon, contentDescription = estado.string, tint = Color(estado.color))
                            }
                        )
                    }
                }
            }

            SelectorUsuarioConBurnout(
                miembros = miembros,
                selectedUsuario = usuarioSeleccionado,
                onUsuarioSelected = { usuarioSeleccionado = it },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = if (fechaVencimiento != null) formatearFecha(fechaVencimiento!!) else "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de vencimiento (opcional)") },
                placeholder = { Text("Sin fecha") },
                trailingIcon = {
                    Row {
                        IconButton(onClick = { mostrarDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha")
                        }
                        if (fechaVencimiento != null) {
                            TextButton(onClick = { fechaVencimiento = null }) { Text("X") }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = textStateDescripcion,
                onValueChange = { textStateDescripcion = it },
                label = { Text("Descripción") },
                placeholder = { Text("Detalles de la tarea...") },
                modifier = Modifier.fillMaxHeight(0.4f).fillMaxWidth().padding(vertical = 8.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { ejecutarEnvio() }),
                singleLine = false
            )
        }
    }
}
