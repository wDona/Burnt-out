package dev.wdona.burntout.presentation.ui.pantallas.ajustes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import dev.wdona.burntout.presentation.ui.components.ajustes.FilaInfo
import dev.wdona.burntout.presentation.ui.components.ajustes.FilaToggleAjuste
import dev.wdona.burntout.presentation.ui.components.ajustes.SeccionAjustesCard
import dev.wdona.burntout.presentation.ui.components.common.DialogoConfirmacionEliminar
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.AjustesViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.AjustesViewModel
import dev.wdona.burntout.shared.db.DatabaseActions
import dev.wdona.burntout.shared.db.eliminarBaseDatosLocal

class SettingsScreen(val factory: AjustesViewModelFactory) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewmodel = rememberScreenModel { factory.create() }
        SettingsContent(
            viewmodel,
            onVolver = { navigator.pop() },
            onLogout = {
                DatabaseActions.recreateDB()
                navigator.popUntilRoot()
            },
            onDBEliminada = { navigator.popUntilRoot() }
        )
    }
}

@Composable
fun SettingsContent(viewModel: AjustesViewModel, onVolver: () -> Unit, onLogout: () -> Unit, onDBEliminada: () -> Unit = {}) {
    val ajustes by viewModel.ajustesUiState.collectAsStateWithLifecycle()
    val respuestasAnonimas by viewModel.respuestasAnonimas.collectAsStateWithLifecycle()
    val notificacionesActivas by viewModel.notificacionesActivas.collectAsStateWithLifecycle()
    val cuentaEliminada by viewModel.cuentaEliminada.collectAsStateWithLifecycle()

    var mostrarDialogoEliminarDB by remember { mutableStateOf(false) }
    var mostrarEliminarCuenta by remember { mutableStateOf(false) }

    LaunchedEffect(cuentaEliminada) {
        if (cuentaEliminada) {
            onLogout()
        }
    }

    if (mostrarDialogoEliminarDB) {
        DialogEliminarDB(
            onConfirmar = {
                mostrarDialogoEliminarDB = false
                viewModel.resetSettings()
                eliminarBaseDatosLocal()
                onDBEliminada()
            },
            onDismiss = { mostrarDialogoEliminarDB = false }
        )
    }

    if (mostrarEliminarCuenta) {
        val username = ajustes.nombreUsuario
        DialogoConfirmacionEliminar(
            username = username,
            tituloInicial = "¿Eliminar tu cuenta?",
            onConfirmar = {
                viewModel.eliminarCuentaPropia()
                mostrarEliminarCuenta = false
            },
            onDismiss = {
                mostrarEliminarCuenta = false
            }
        )
    }

    ScaffoldBase(titulo = "Ajustes", onVolver = onVolver) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SeccionAjustesCard("Privacidad") {
                FilaToggleAjuste(
                    titulo = "Respuestas anónimas",
                    descripcion = "Tus respuestas al cuestionario no mostrarán tu nombre",
                    checked = respuestasAnonimas,
                    onToggle = { viewModel.toggleRespuestasAnonimas() }
                )
            }

            SeccionAjustesCard("Notificaciones") {
                FilaToggleAjuste(
                    titulo = "Notificaciones activas",
                    descripcion = "Recibir alertas de tareas con fecha de vencimiento",
                    checked = notificacionesActivas,
                    onToggle = { viewModel.toggleNotificacionesActivas() }
                )
            }

            SeccionAjustesCard("Información") {
                FilaInfo("Versión", ajustes.versionApp)
            }

            TextButton(
                onClick = { mostrarDialogoEliminarDB = true },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Eliminar base de datos local")
            }

            if (ajustes.idUsuario != Long.MIN_VALUE) {
                TextButton(
                    onClick = { mostrarEliminarCuenta = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Eliminar mi cuenta")
                }
            }

            TextButton(
                onClick = { viewModel.resetSettings(); onLogout() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Cerrar sesión")
            }
        }
    }
}

@Composable
private fun DialogEliminarDB(onConfirmar: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
        title = { Text("Eliminar base de datos local") },
        text = { Text("Se eliminará el archivo de base de datos local. La aplicación se cerrará automáticamente.") },
        confirmButton = {
            TextButton(
                onClick = onConfirmar,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) { Text("Eliminar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
