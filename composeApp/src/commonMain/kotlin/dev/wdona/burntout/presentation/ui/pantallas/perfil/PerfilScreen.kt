package dev.wdona.burntout.presentation.ui.pantallas.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.MiPerfilViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.PerfilViewModel
import dev.wdona.burntout.shared.utils.SettingsManager
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import dev.wdona.burntout.presentation.ui.pantallas.SettingsScreen
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.AjustesViewModelFactory
import dev.wdona.burntout.presentation.ui.components.common.BateriaBurnout

class PerfilScreen(val factory: MiPerfilViewModelFactory, val ajustesFactory: AjustesViewModelFactory, val onVolver: (() -> Unit)? = null, var idUsuario: Long? = null) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val viewmodel = rememberScreenModel { factory.create() }
        if (idUsuario == null) {
            idUsuario = SettingsManager.getIdUsuarioActual()
        }

        LaunchedEffect(idUsuario) {
            viewmodel.cargarUsuario(idUsuario!!)
        }

        PerfilContent(
            viewmodel,
            onAjustes = { navigator.push(SettingsScreen(ajustesFactory)) },
            onVolver = onVolver
        )
    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
fun PerfilContent(viewModel: PerfilViewModel, onAjustes: () -> Unit, onVolver: (() -> Unit)? = null) {
    // Hacer que uiState sea un objeto para refrescar todos los elementos a la vez
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val usuario = uiState.usuario
    val isLoading = uiState.isLoading

    val titulo = if (isLoading) "" else (usuario?.nombre ?: "No se ha cargado el usuario")

    val titleIcon = @Composable {
        if (isLoading) {
             // TODO: placeholders como en youtube
        } else if (usuario != null) {
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
        onAjustes = onAjustes,
        onVolver = onVolver,
        titleIcon = titleIcon
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val tamanioIconoResponsive = (maxWidth * 0.5f).coerceIn(100.dp, 300.dp)

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                if (isLoading) {
//                    CircularProgressIndicator(
//                        modifier = Modifier
//                            .padding(bottom = 8.dp)
//                    )
                    // TODO: PLACEHOLDER como en youtueb
//                    Text(
//                        text = "Cargando perfil...",
//                        style = MaterialTheme.typography.titleLarge,
//                        modifier = Modifier.padding(16.dp)
//                    )
                } else if (usuario == null) {
                    Text(
                        text = "No se ha podido cargar el usuario",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    OutlinedButton(
                        onClick = {
                            if (onVolver != null) {
                                onVolver()
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                    ) {
                        Text("Volver")
                    }
                }
                else {
                    val riesgo = usuario?.riesgoBurnout ?: 0.0

                    BateriaBurnout(riesgo = riesgo)

//                    Text("ID: ${usuario!!.idUsuario}", style = MaterialTheme.typography.titleMedium)
                    Text(usuario!!.username, style = MaterialTheme.typography.titleMedium)
                    Text(usuario!!.descripcion ?: "-", style = MaterialTheme.typography.titleMedium)

//                    if (usuario!!.idUsuario == SettingsManager.getIdUsuarioActual()) {
//                        OutlinedButton(
//                            onClick = {
////                                TODO: ELIMINAR DATOS DE SESION DE SETTINGS Y POP UNTIL ROOT
//                            },
//                            modifier = Modifier
//                                .padding(top = 32.dp)
//                                .fillMaxWidth()
//                                .padding(horizontal = 32.dp)
//                                .height(56.dp),
//                            shape = RoundedCornerShape(28.dp),
//                            border = BorderStroke(1.dp, BurntOutMaterialTheme.getColorScheme().error),
//                            colors = ButtonDefaults.outlinedButtonColors(
//                                contentColor = BurntOutMaterialTheme.getColorScheme().error
//                            )
//                        ) {
//                            Icon(
//                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
//                                contentDescription = "Cerrar sesion",
//                                modifier = Modifier.padding(end = 8.dp)
//                            )
//                            Text("Cerrar sesion")
//                        }
//                    }
                }
            }
        }
    }
}
