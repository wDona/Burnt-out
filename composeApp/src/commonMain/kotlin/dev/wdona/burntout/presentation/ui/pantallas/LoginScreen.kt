package dev.wdona.burntout.presentation.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.LoginViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.LoginViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.ui.theme.BurntOutMaterialTheme
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.AjustesViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.AjustesViewModel
import dev.wdona.burntout.shared.utils.SettingsManager

class LoginScreen(private val factory: LoginViewModelFactory, private val settingsFactory: AjustesViewModelFactory) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { factory.create() }
        val settingsViewModel = rememberScreenModel { settingsFactory.create() }

        LoginContent(
            viewModel = viewModel,
            settingsViewModel = settingsViewModel
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoginContent(
    viewModel: LoginViewModel,
    settingsViewModel: AjustesViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.clearSuccess()
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var textStateUsuario by remember { mutableStateOf("") }
    var textStatePassword by remember { mutableStateOf("") }
    var textStateNombre by remember { mutableStateOf("") }

    val isError = uiState.error != null
    val success = uiState.success

    val focusManager = LocalFocusManager.current

    val enviarAccion = {
        if (uiState.isLogin) {
            viewModel.login(textStateUsuario, textStatePassword, settingsViewModel)
        } else {
            viewModel.register(textStateUsuario, textStatePassword, textStateNombre.trim(), settingsViewModel)
        }
        textStateUsuario = ""
        textStatePassword = ""
        textStateNombre = ""
        focusManager.clearFocus()

        if (success) {
            uiState.isLogin = true
        }
    }

    ScaffoldBase {
        Column (
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ){
            Column (
                modifier = Modifier.width(340.dp),
            ){
                Text(
                    text = if (uiState.isLogin) "Login" else "Registrate",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    style = MaterialTheme.typography.bodySmall.copy(color = BurntOutMaterialTheme.getWarningColor()),
                    text = "No uses contrasenas privadas, \ncualquiera podra acceder a tu cuenta",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                if (!uiState.isLogin) {
                    OutlinedTextField(
                        value = textStateNombre,
                        onValueChange = { textStateNombre = it },
                        label = { Text("Nombre") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        modifier = Modifier.onKeyEvent {
                            if (it.type == KeyEventType.KeyUp && (it.key == Key.Enter || it.key == Key.NumPadEnter)) {
                                focusManager.moveFocus(FocusDirection.Down)
                                true
                            } else false
                        },
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = textStateUsuario,
                    onValueChange = {
                        if (it.contains(' ')) return@OutlinedTextField
                        textStateUsuario = it.trim() },
                    label = { Text("Usuario") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    modifier = Modifier.onKeyEvent {
                        if (it.type == KeyEventType.KeyUp && (it.key == Key.Enter || it.key == Key.NumPadEnter)) {
                            focusManager.moveFocus(FocusDirection.Down)
                            true
                        } else false
                    },
                    singleLine = true
                )
                OutlinedTextField(
                    value = textStatePassword,
                    onValueChange = { textStatePassword = it.trim() },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { enviarAccion() }),
                    modifier = Modifier.onKeyEvent {
                        if (it.type == KeyEventType.KeyUp && (it.key == Key.Enter || it.key == Key.NumPadEnter)) {
                            enviarAccion()
                            true
                        } else false
                    },
                    singleLine = true
                )

                if (uiState.isLoading) {
                    Text(
                        text = "Cargando...",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else if (isError) {
                    Text(
                        text = "Error: ${uiState.error}",
                        style = MaterialTheme.typography.bodySmall
                            .copy(color = BurntOutMaterialTheme.getErrorColor())
                    )
                } else if (success) {
                    Text(
                        text = "Success",
                        style = MaterialTheme.typography.bodySmall
                            .copy(color = BurntOutMaterialTheme.getSuccessColor())
                    )
                }

                TextButton(
                    onClick = {
                        viewModel.toggleMode()
                    }
                ) {
                    Text(if (uiState.isLogin) "¿No tienes cuenta? Regístrate" else "¿Ya tienes cuenta? Login")
                }

                Button(
                    onClick = enviarAccion,
                    enabled = !uiState.isLoading && textStateUsuario.isNotBlank() && textStatePassword.isNotBlank()
                            && (uiState.isLogin || textStateNombre.isNotBlank())
                ) {
                    Text(if (uiState.isLogin) "Login" else "Registrate")
                }

                TextButton(
                    onClick = {
                        SettingsManager.setUsuarioInvitado()
                    }
                ) {
                    Text(
                        text = "Entrar como invitado",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

        }
    }
}