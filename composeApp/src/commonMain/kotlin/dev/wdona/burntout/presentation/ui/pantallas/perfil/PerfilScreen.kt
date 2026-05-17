package dev.wdona.burntout.presentation.ui.pantallas.perfil

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.MiPerfilViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.PerfilViewModel
import dev.wdona.burntout.shared.utils.SettingsManager
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import dev.wdona.burntout.presentation.ui.pantallas.ajustes.SettingsScreen
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.AjustesViewModelFactory
import dev.wdona.burntout.presentation.ui.components.common.BateriaBurnout
import dev.wdona.burntout.presentation.ui.components.common.FilaTextoPlaceholder
import dev.wdona.burntout.presentation.viewmodel.viewmodels.AjustesViewModel
import androidx.compose.runtime.collectAsState

class PerfilScreen(val factory: MiPerfilViewModelFactory, val ajustesFactory: AjustesViewModelFactory, val onVolver: (() -> Unit)? = null, var idUsuario: Long? = null) : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val viewmodel = rememberScreenModel { factory.create() }
        val settingsViewModel = rememberScreenModel { ajustesFactory.create() }

        val idUsuarioActual by SettingsManager.idUsuarioActualFlow.collectAsState()
        val targetId = idUsuario ?: idUsuarioActual

        LaunchedEffect(targetId) {
            viewmodel.cargarUsuario(targetId)
        }

        PerfilContent(
            viewmodel,
            onAjustes = { navigator.push(SettingsScreen(ajustesFactory)) },
            onVolver = onVolver,
            idUsuarioActual = idUsuarioActual
        )
    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
fun PerfilContent(
    viewModel: PerfilViewModel,
    onAjustes: () -> Unit,
    onVolver: (() -> Unit)? = null,
    idUsuarioActual: Long
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var modoEdicion by remember { mutableStateOf(false) }
    var nombreEdit by remember { mutableStateOf("") }
    var descripcionEdit by remember { mutableStateOf("") }

    val titulo = if (uiState.isLoading) "" else (uiState.usuario?.nombre ?: "No se ha cargado el usuario")

    val titleIcon = @Composable {
        if (uiState.isLoading) {
             // TODO: placeholders
        } else if (uiState.usuario != null) {
            Icon(
                imageVector = Icons.Default.Person4,
                contentDescription = "Icono de usuario",
                modifier = Modifier.padding(end = 8.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = "Icono de nube tachada",
                modifier = Modifier
                    .alpha(0.2f)
                    .padding(end = 8.dp)
            )
        }
    }

    ScaffoldBase(
        titulo = titulo,
        subtitle = if (uiState.isLoading) "" else if (uiState.usuario != null) "@" + uiState.usuario!!.username else "No se ha podido cargar el usuario",
        onAjustes = onAjustes,
        onVolver = onVolver,
        titleIcon = titleIcon
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val tamanioIconoResponsive = (maxWidth * 1f).coerceIn(100.dp, 800.dp)

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                if (uiState.isLoading) {
                    FilaTextoPlaceholder(
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .width(tamanioIconoResponsive),
                        paddingBottom = 16,
                        height = 28
                    )
                    FilaTextoPlaceholder(
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .width(tamanioIconoResponsive),
                        paddingBottom = 8,
                        height = 28
                    )
                    FilaTextoPlaceholder(
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .width(tamanioIconoResponsive),
                        height = 64,
                        paddingBottom = 8,
                        paddingTop = 8

                    )
                } else if (uiState.usuario == null) {
                    Text(
                        text = "No se ha podido cargar el usuario",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    if (onVolver != null) {
                        OutlinedButton(
                            onClick = {
                                onVolver()
                            },
                            shape = RoundedCornerShape(24.dp),
                        ) {
                            Text("Volver")
                        }
                    }
                }
                else {
                    val usuario = uiState.usuario!!
                    val riesgo = usuario.riesgoBurnout ?: -1.0
                    val esPropioUsuario = usuario.idUsuario == idUsuarioActual

                    BateriaBurnout(riesgo = riesgo)

                    if (modoEdicion && esPropioUsuario) {
                        OutlinedTextField(
                            value = nombreEdit,
                            onValueChange = { nombreEdit = it },
                            label = { Text("Nombre") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp, vertical = 4.dp)
                        )
                        OutlinedTextField(
                            value = descripcionEdit,
                            onValueChange = { descripcionEdit = it },
                            label = { Text("Descripción") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp, vertical = 4.dp)
                        )
                        OutlinedButton(
                            onClick = {
                                if (nombreEdit.isNotBlank()) {
                                    viewModel.actualizarPerfil(
                                        usuario.copy(
                                            nombre = nombreEdit.trim(),
                                            descripcion = descripcionEdit.trim().ifBlank { "Sin descripción" }
                                        )
                                    )
                                }
                                modoEdicion = false
                            },
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text("Guardar")
                        }
                        TextButton(onClick = { modoEdicion = false }, modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)) {
                            Text("Cancelar")
                        }
                    } else {
//                        Text(usuario.username, style = MaterialTheme.typography.titleMedium)
//                        Text(usuario.nombre, style = MaterialTheme.typography.bodyLarge)
                        Text(usuario.descripcion?.ifBlank { "Sin descripción" } ?: "Sin descripción", style = MaterialTheme.typography.bodyMedium)

                        if (esPropioUsuario) {
                            TextButton(
                                onClick = {
                                    nombreEdit = usuario.nombre
                                    descripcionEdit = if (usuario.descripcion == "Sin descripción") "" else usuario.descripcion ?: ""
                                    modoEdicion = true
                                },
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .fillMaxWidth()
                                    .padding(horizontal = 32.dp),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar perfil",
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("Editar perfil")
                            }

                        }
                    }
                }
            }
        }
    }
}
