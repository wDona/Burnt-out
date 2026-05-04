package dev.wdona.burntout.presentation.ui.pantallas.tarea

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import dev.wdona.burntout.shared.domain.Subtarea
import dev.wdona.burntout.shared.domain.Tarea
import dev.wdona.burntout.shared.domain.Usuario
import dev.wdona.burntout.shared.utils.SettingsManager

class TareaDetalleScreen(private val idTarea: Long, private val idTablero: Long, private val factory: TareasViewModelFactory) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { factory.create() }

        LaunchedEffect(idTarea, idTablero) {
            viewModel.cargarTareaPorId(idTarea, idTablero)
            viewModel.cargarMiembrosEquipo(SettingsManager.getIdEquipoActual())
            viewModel.cargarSubtareas(idTarea)
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
    val miembros by viewModel.miembros.collectAsState()
    val subtareas by viewModel.subtareas.collectAsState()

    var textStateNombreTarea by remember { mutableStateOf("") }
    var textStateDescripcion by remember { mutableStateOf("") }
    var nuevaSubtareaTitulo by remember { mutableStateOf("") }

    var estadoExpanded by remember { mutableStateOf(false) }
    var estadoSelected by remember { mutableStateOf(TipoEstadoTarea.PENDIENTE) }

    var usuarioSeleccionado by remember { mutableStateOf<Usuario?>(null) }
    var mostrarDialogBurnout by remember { mutableStateOf(false) }

    var mostrarDatePicker by remember { mutableStateOf(false) }
    var fechaVencimiento by remember { mutableStateOf<Long?>(null) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(tarea) {
        textStateNombreTarea = tarea?.titulo ?: ""
        textStateDescripcion = tarea?.descripcion ?: ""
        fechaVencimiento = tarea?.fechaVencimiento
        for (estado in TipoEstadoTarea.entries) {
            if (estado.string.equals(tarea?.estado, ignoreCase = true)) {
                estadoSelected = estado
                break
            }
        }
    }

    LaunchedEffect(tarea, miembros) {
        if (tarea != null && miembros.isNotEmpty() && usuarioSeleccionado == null) {
            usuarioSeleccionado = miembros.firstOrNull { it.idUsuario == tarea!!.idUsuarioAsignado }
                ?: miembros.firstOrNull { it.idUsuario == SettingsManager.getIdUsuarioActual() }
                ?: miembros.first()
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
            val tareaActualizada = Tarea(
                idTarea = tarea?.idTarea ?: Long.MIN_VALUE,
                titulo = textStateNombreTarea,
                descripcion = textStateDescripcion,
                estado = estadoSelected.string,
                idTableroPerteneciente = tarea?.idTableroPerteneciente ?: Long.MIN_VALUE,
                idUsuarioAsignado = usuarioSeleccionado?.idUsuario ?: SettingsManager.getIdUsuarioActual(),
                idSubtareas = emptyList(),
                fechaVencimiento = fechaVencimiento
            )
            if (estadoSelected == TipoEstadoTarea.COMPLETADA) {
                viewModel.completarTarea(tareaActualizada, SettingsManager.getIdEquipoActual())
            } else {
                viewModel.actualizarTarea(tareaActualizada)
            }
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
        titulo = "Editar Tarea: " + (tarea?.titulo ?: ""),
        onVolver = { onVolver() },
        onFAB = { ejecutarEnvio() },
        textoFAB = "Editar Tarea"
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
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
                label = { Text("Descripcion") },
                placeholder = { Text("Detalles de la tarea...") },
                modifier = Modifier.heightIn(min = 150.dp).fillMaxWidth().padding(vertical = 8.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { ejecutarEnvio() }),
                singleLine = false
            )

            // Subtareas
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = "Subtareas",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
            )

            subtareas.forEach { subtarea ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { viewModel.toggleSubtarea(subtarea) }) {
                        Icon(
                            imageVector = if (subtarea.completado) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                            contentDescription = if (subtarea.completado) "Completada" else "Pendiente",
                            tint = if (subtarea.completado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = subtarea.titulo,
                        modifier = Modifier.weight(1f),
                        color = if (subtarea.completado) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = { viewModel.eliminarSubtarea(subtarea.idSubtarea, subtarea.idTareaPerteneciente) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar subtarea", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            ) {
                OutlinedTextField(
                    value = nuevaSubtareaTitulo,
                    onValueChange = { nuevaSubtareaTitulo = it },
                    label = { Text("Nueva subtarea") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (nuevaSubtareaTitulo.isNotBlank() && tarea != null) {
                            viewModel.crearSubtarea(
                                Subtarea(
                                    idSubtarea = 0,
                                    titulo = nuevaSubtareaTitulo.trim(),
                                    descripcion = null,
                                    completado = false,
                                    idTareaPerteneciente = tarea!!.idTarea
                                )
                            )
                            nuevaSubtareaTitulo = ""
                        }
                    })
                )
                IconButton(
                    onClick = {
                        if (nuevaSubtareaTitulo.isNotBlank() && tarea != null) {
                            viewModel.crearSubtarea(
                                Subtarea(
                                    idSubtarea = 0,
                                    titulo = nuevaSubtareaTitulo.trim(),
                                    descripcion = null,
                                    completado = false,
                                    idTareaPerteneciente = tarea!!.idTarea
                                )
                            )
                            nuevaSubtareaTitulo = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar subtarea")
                }
            }
        }
    }
}
