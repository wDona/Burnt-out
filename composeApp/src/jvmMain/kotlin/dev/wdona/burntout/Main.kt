package dev.wdona.burntout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.wdona.burntout.presentation.ui.theme.BurntOutMaterialTheme
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.*
import dev.wdona.burntout.shared.db.DatabaseDriverFactory
import dev.wdona.burntout.shared.db.DatabaseInit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.res.painterResource

fun main() = application {
    var isDatabaseReady by remember { mutableStateOf(false) }
    var databaseError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            withContext(Dispatchers.IO) {
                val driver = DatabaseDriverFactory().createDriver()
                DatabaseInit.init(driver)
            }
            isDatabaseReady = true
        } catch (e: Exception) {
            println("Error al inicializar la base de datos: ${e.message}")
            e.printStackTrace()
            databaseError = "Error al iniciar la base de datos.\n\n" +
                    "Es probable que el esquema de la base de datos haya cambiado.\n\n" +
                    "Por favor, ve a la carpeta de usuario/.burntout_app y elimina el archivo 'burntout.db' para reiniciar la aplicación.\n\n" +
                    "Detalle: ${e.message}"
        }
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Burn't out",
        icon = painterResource("logoBurntOutIcon.png")
    ) {
        BurntOutMaterialTheme {
            Surface (
                modifier = Modifier.fillMaxSize(),
                color = BurntOutMaterialTheme.getColorScheme().background
            ){
                if (databaseError != null) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "¡Ups! Algo salió mal, vuelve a intentarlo, reinstala la aplicacion " +
                                        "o elimina la carpeta .burntout_app (/home/tuusuario/.burntout_app o " +
                                        "C:/Users/tuusuario/.burntout_app)",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = databaseError!!,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            Button(onClick = ::exitApplication) {
                                Text("Cerrar aplicación")
                            }
                        }
                    }
                } else if (isDatabaseReady) {
                    App(
                        tareaFactory = TareasViewModelFactory(),
                        equipoViewModelFactory = EquipoViewModelFactory(),
                        miPerfilViewModelFactory = MiPerfilViewModelFactory(),
                        tablerosViewModelFactory = TablerosViewModelFactory(),
                        leaderboardViewModelFactory = LeaderboardViewModelFactory(),
                        formularioViewModelFactory = FormularioViewModelFactory(),
                        ajustesViewModelFactory = AjustesViewModelFactory(),
                        operacionesPendientesViewModelFactory = OperacionesPendientesViewModelFactory(),
                        loginViewModelFactory = LoginViewModelFactory()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
