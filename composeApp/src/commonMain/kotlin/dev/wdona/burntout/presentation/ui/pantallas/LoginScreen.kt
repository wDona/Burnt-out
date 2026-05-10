package dev.wdona.burntout.presentation.ui.pantallas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase
import dev.wdona.burntout.presentation.ui.theme.BurntOutMaterialTheme
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.AjustesViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.LoginViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.AjustesViewModel
import dev.wdona.burntout.presentation.viewmodel.viewmodels.LoginViewModel
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

    var textStateNombre by remember { mutableStateOf("") }
    var textStateUsuario by remember { mutableStateOf("") }
    var textStatePassword by remember { mutableStateOf("") }
    var modoRegistro by remember { mutableStateOf("CREAR_ORG") }
    var textStateNombreOrg by remember { mutableStateOf("") }
    var textStateCodigo by remember { mutableStateOf("") }
    var modificado by remember { mutableStateOf(false) }
    var isLogin by remember { mutableStateOf(true) }
    var usarHostPersonalizado by remember { mutableStateOf(SettingsManager.isUsandoHostPersonalizado()) }
    var hostPersonalizado by remember { mutableStateOf(SettingsManager.getHostPersonalizado()) }


    uiState.isLogin = isLogin

    val isError = uiState.error != null
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState.isLogin) {
        modoRegistro = "CREAR_ORG"
        textStateNombreOrg = ""
        textStateCodigo = ""
    }

    val enviarAccion = {
        if (isLogin) {
            viewModel.login(textStateUsuario, textStatePassword, settingsViewModel)
        } else {
            viewModel.register(
                username = textStateUsuario,
                contrasena = textStatePassword,
                nombre = textStateNombre.trim(),
                settingsViewModel = settingsViewModel,
                modo = modoRegistro,
                nombreOrg = textStateNombreOrg.trim().ifBlank { null },
                codigoInvitacion = textStateCodigo.trim().ifBlank { null }
            )
        }
        textStateUsuario = ""
        textStatePassword = ""
        textStateNombre = ""
        textStateNombreOrg = ""
        textStateCodigo = ""
        focusManager.clearFocus()
    }

    val registroValido = textStateNombre.isNotBlank() &&
            (modoRegistro == "CREAR_ORG" || textStateCodigo.isNotBlank())

    ScaffoldBase {
        Column(
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .width(340.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = if (isLogin) "Login" else "Registrate",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                if (!isLogin) {
                    Text(
                        style = MaterialTheme.typography.bodySmall.copy(color = BurntOutMaterialTheme.getWarningColor()),
                        text = "No uses contrasenas privadas, \ncualquiera podra acceder a tu cuenta",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                AnimatedVisibility(!isLogin) {
                    OutlinedTextField(
                        value = textStateNombre,
                        onValueChange = { textStateNombre = it },
                        label = { Text("Nombre*") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onKeyEvent {
                                if (it.type == KeyEventType.KeyUp && (it.key == Key.Enter || it.key == Key.NumPadEnter)) {
                                    focusManager.moveFocus(FocusDirection.Down); true
                                } else false
                            },
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = textStateUsuario,
                    onValueChange = {
                        if (it.contains(' ')) return@OutlinedTextField
                        textStateUsuario = it.trim()
                    },
                    label = { Text("Usuario*") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onKeyEvent {
                            if (it.type == KeyEventType.KeyUp && (it.key == Key.Enter || it.key == Key.NumPadEnter)) {
                                focusManager.moveFocus(FocusDirection.Down); true
                            } else false
                        },
                    singleLine = true
                )

                OutlinedTextField(
                    value = textStatePassword,
                    onValueChange = { textStatePassword = it.trim() },
                    label = { Text("Password*") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { enviarAccion() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onKeyEvent {
                            if (it.type == KeyEventType.KeyUp && (it.key == Key.Enter || it.key == Key.NumPadEnter)) {
                                enviarAccion(); true
                            } else false
                        },
                    singleLine = true
                )

                if (uiState.isLoading) {
                    Text(
                        text = "Cargando...",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else if (isError) {
                    Text(
                        text = "Error: ${uiState.error}",
                        style = MaterialTheme.typography.bodySmall
                            .copy(color = BurntOutMaterialTheme.getErrorColor()),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else if (uiState.success) {
                    Text(
                        text = "Success",
                        style = MaterialTheme.typography.bodySmall
                            .copy(color = BurntOutMaterialTheme.getSuccessColor()),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                AnimatedVisibility(!isLogin) {
                    Column {
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            SegmentedButton(
                                selected = modoRegistro == "CREAR_ORG",
                                onClick = { modoRegistro = "CREAR_ORG"; textStateCodigo = "" },
                                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                                label = { Text("Crear org") }
                            )
                            SegmentedButton(
                                selected = modoRegistro == "UNIRSE",
                                onClick = { modoRegistro = "UNIRSE"; textStateNombreOrg = "" },
                                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                                label = { Text("Unirme a org") }
                            )
                        }

                        AnimatedVisibility(modoRegistro == "CREAR_ORG") {
                            OutlinedTextField(
                                value = if (modificado) {
                                    textStateNombreOrg
                                } else if (textStateNombre.isNotBlank()) {
                                    "Organizacion de $textStateNombre"
                                } else {
                                    "Mi Organizacion"
                                },
                                onValueChange = {
                                    textStateNombreOrg = it
                                    modificado = true
                                },
                                label = { Text("Nombre de la organización*") },
                                placeholder = { Text("Org de ${textStateNombre.ifBlank { "Mi Organizacion" }}") },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
                                singleLine = true
                            )
                        }

                        AnimatedVisibility(modoRegistro == "UNIRSE") {
                            OutlinedTextField(
                                value = textStateCodigo,
                                onValueChange = { textStateCodigo = it.uppercase().trim() },
                                label = { Text("Código de invitación*") },
                                placeholder = { Text("ACME-X7K2PQ") },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
                                singleLine = true
                            )
                        }
                    }
                }

                TextButton(onClick = { isLogin = !isLogin }) {
                    Text(
                        text = if (isLogin) "¿No tienes cuenta? Regístrate" else "¿Ya tienes cuenta? Login",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Row {
                    Button(
                        onClick = enviarAccion,
                        enabled = !uiState.isLoading
                                && textStateUsuario.isNotBlank()
                                && textStatePassword.isNotBlank()
                                && (isLogin || registroValido),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(if (isLogin) "Login" else "Registrate")
                    }

                    TextButton(onClick = { SettingsManager.setUsuarioInvitado() }) {
                        Text(
                            text = "Entrar como invitado",
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                TextButton(
                    onClick = {
                        usarHostPersonalizado = !usarHostPersonalizado
                        SettingsManager.setUsarHostPersonalizado(usarHostPersonalizado)
                    }
                ) {
                    Text(
                        text = if (usarHostPersonalizado) "Usar servidor por defecto" else "Usar otro servidor",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                AnimatedVisibility(usarHostPersonalizado) {
                    OutlinedTextField(
                        value = hostPersonalizado,
                        onValueChange = {
                            hostPersonalizado = it.trim()
                            SettingsManager.setHostPersonalizado(it.trim())
                        },
                        label = { Text("Host del servidor") },
                        placeholder = { Text("ej: 192.168.1.10") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }
    }
}
