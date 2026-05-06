package dev.wdona.burntout.presentation.ui.pantallas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
            onDBEliminada = {
                navigator.popUntilRoot()
            }
        )
    }
}

@Composable
fun SettingsContent(viewModel: AjustesViewModel, onVolver: () -> Unit, onLogout: () -> Unit, onDBEliminada: () -> Unit = {}) {
    val ajustes by viewModel.ajustesUiState.collectAsStateWithLifecycle()
    val respuestasAnonimas by viewModel.respuestasAnonimas.collectAsStateWithLifecycle()
    var mostrarDialogoEliminarDB by remember { mutableStateOf(false) }

    if (mostrarDialogoEliminarDB) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminarDB = false },
            icon = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Eliminar base de datos local") },
            text = { Text("Se eliminará el archivo de base de datos local. La aplicación se cerrará automáticamente.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoEliminarDB = false
                        viewModel.resetSettings()
                        eliminarBaseDatosLocal()
                        onDBEliminada()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminarDB = false }) { Text("Cancelar") }
            }
        )
    }

    ScaffoldBase(
        titulo = "Ajustes",
        onVolver = onVolver,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = "Privacidad",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { viewModel.toggleRespuestasAnonimas() }.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Respuestas anónimas",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Tus respuestas al cuestionario no mostrarán tu nombre",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = respuestasAnonimas,
                            onCheckedChange = { viewModel.toggleRespuestasAnonimas() },
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = "Información",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    FilaInfo("Versión", ajustes.versionApp)
//                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
//                    FilaInfo("Organización", ajustes.idOrganizacion.toString())
                }
            }

            TextButton(
                onClick = { mostrarDialogoEliminarDB = true },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Eliminar base de datos local")
            }

            TextButton(
                onClick = {
                    viewModel.resetSettings()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth(),
//                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Cerrar sesión")
            }
        }
    }
}

@Composable
private fun FilaInfo(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = etiqueta, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
