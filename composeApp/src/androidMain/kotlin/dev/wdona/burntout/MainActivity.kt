package dev.wdona.burntout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import dev.wdona.burntout.presentation.ui.theme.BurntOutMaterialTheme
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.*
import dev.wdona.burntout.shared.db.DatabaseDriverFactory
import dev.wdona.burntout.shared.db.DatabaseInit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            var isDatabaseReady by remember { mutableStateOf(false) }
            var databaseError by remember { mutableStateOf<String?>(null) }
            
            LaunchedEffect(Unit) {
                try {
                    withContext(Dispatchers.IO) {
                        val driver = DatabaseDriverFactory(applicationContext).createDriver()
                        DatabaseInit.init(driver)
                    }
                    isDatabaseReady = true
                } catch (e: Exception) {
                    println("Error al inicializar la base de datos: ${e.message}")
                    e.printStackTrace()
                    databaseError = "Error al iniciar la base de datos.\n\n" +
                            "El esquema de la base de datos ha cambiado.\n\n" +
                            "Para solucionar esto, por favor borra los datos de almacenamiento de la aplicación o desinstálala y vuelve a instalarla.\n\n" +
                            "Detalle: ${e.message}"
                }
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = BurntOutMaterialTheme.getColorScheme().background
            ) {
                 if (databaseError != null) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "¡Ups! Algo salió mal",
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
                            Button(onClick = { finish() }) {
                                Text("Cerrar aplicación")
                            }
                        }
                    }
                } else if (!isDatabaseReady) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    val tareaFactory = remember { TareasViewModelFactory(applicationContext) }
                    val miEquipoViewModelFactory = remember { MiEquipoViewModelFactory(applicationContext) }
                    val miPerfilViewModelFactory = remember { MiPerfilViewModelFactory(applicationContext) }
                    val tablerosViewModelFactory = remember { TablerosViewModelFactory(applicationContext) }
                    val leaderboardViewModelFactory = remember { LeaderboardViewModelFactory(applicationContext) }
                    val ajustesViewModelFactory = remember { AjustesViewModelFactory(applicationContext) }
                    val formularioViewModelFactory = remember { FormularioViewModelFactory(applicationContext) }
                    val operacionesPendientesViewModelFactory = remember { OperacionesPendientesViewModelFactory(applicationContext) }

                    App(
                        tareaFactory,
                        miEquipoViewModelFactory,
                        miPerfilViewModelFactory,
                        tablerosViewModelFactory,
                        leaderboardViewModelFactory,
                        ajustesViewModelFactory,
                        formularioViewModelFactory,
                        operacionesPendientesViewModelFactory
                    )
                }
            }
        }
    }
}
