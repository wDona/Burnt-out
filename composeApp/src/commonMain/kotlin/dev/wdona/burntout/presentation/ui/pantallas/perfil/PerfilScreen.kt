package dev.wdona.burntout.presentation.ui.pantallas.perfil

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Person4
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
import dev.wdona.burntout.presentation.ui.theme.BurntOutMaterialTheme
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.AjustesViewModelFactory
import kotlin.math.round

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
    val usuario by viewModel.usuarioActual.collectAsStateWithLifecycle()

    val titleIcon = @Composable {
        if (usuario != null) {
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
        titulo = usuario?.nombre ?: "No se ha cargado el usuario",
        onAjustes = onAjustes,
        onVolver = onVolver,
        titleIcon = titleIcon
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val tamanioIconoResponsive = (maxWidth * 0.5f).coerceIn(100.dp, 300.dp)

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (usuario == null) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = "Icono de nube tachada",
                        modifier = Modifier
                            .size(tamanioIconoResponsive)
                            .padding(bottom = 8.dp)
                            .alpha(0.2f)
                    )
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
                } else {
                    val riesgo = usuario?.riesgoBurnout ?: 0.0
                    val riesgoVisual = round(riesgo * 100) / 100.0

                    Text(
                        text =
                            if (riesgo < 0.33) "Riesgo de Burnout bajo ($riesgoVisual)"
                            else if (riesgo in 0.33..<0.66) "Riesgo de Burnout mediano ($riesgoVisual)"
                            else "Riesgo de Burnout alto ($riesgoVisual)",
                        style = MaterialTheme.typography.titleMedium,
                        color =
                            if (riesgo < 0.33) BurntOutMaterialTheme.getSuccessColor()
                            else if (riesgo in 0.33..<0.66) BurntOutMaterialTheme.getWarningColor()
                            else BurntOutMaterialTheme.getErrorColor()
                    )
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
