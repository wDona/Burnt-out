package dev.wdona.burntout.presentation.ui.pantallas

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.LoginViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodels.LoginViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.wdona.burntout.presentation.ui.components.template.ScaffoldBase

class LoginScreen(private val factory: LoginViewModelFactory) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { factory.create() }

        LoginContent(
            viewModel = viewModel
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoginContent(
    viewModel: LoginViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var textStateUsuario by remember { mutableStateOf("") }
    var textStatePassword by remember { mutableStateOf("") }


    var isLogin = uiState.isLogin
    val isLoading = uiState.isLoading
    val isError = uiState.error != null
    val success = uiState.success

    ScaffoldBase {
        Column {
            Text(
                text = if (isLogin) "Login" else "Registrate",
                style = MaterialTheme.typography.titleLarge
            )
            OutlinedTextField(
                value = textStateUsuario,
                onValueChange = { textStateUsuario = it },
                label = { Text("Usuario") }
            )
            OutlinedTextField(
                value = textStatePassword,
                onValueChange = { textStatePassword = it }, // FIXME ASTERISCOS
                label = { Text("Password") }
            )

            if (isLoading) {
                Text("Cargando...")
            } else if (isError) {
                Text("Error: ${uiState.error}")
            } else if (success) {
                Text("Success")
            }

            if (isLogin) {
                TextButton(
                    onClick = {
                        isLogin = false
                    }
                ) {
                    Text("No tienes cuenta? Registrate")
                }
            } else {
                TextButton(
                    onClick = {
                        isLogin = true
                    }
                ) {
                    Text("Ya tienes cuenta? Login")
                }
            }

            Button(
                onClick = {
                    if (isLogin) {
                        viewModel.login(textStateUsuario, textStatePassword)
                    } else {
                        viewModel.register(textStateUsuario, textStatePassword, "Nombre") // FIXME nombre
                    }
                },
                enabled = !isLoading
            ) {
                Text(if (isLogin) "Login" else "Registrate")
            }
        }
    }
}