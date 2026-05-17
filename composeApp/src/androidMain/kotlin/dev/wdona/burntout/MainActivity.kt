package dev.wdona.burntout

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import dev.wdona.burntout.platform.NetworkObserver
import dev.wdona.burntout.presentation.ui.theme.BurntOutMaterialTheme
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.*
import dev.wdona.burntout.shared.db.DatabaseDriverFactory
import dev.wdona.burntout.shared.db.DatabaseActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            var isDatabaseReady by remember { mutableStateOf(DatabaseActions.isInitialized()) }
            var databaseError by remember { mutableStateOf<String?>(null) }

            val notifLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { /* resultado ignorado, el usuario decidió */ }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    try {
                        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        if (!am.canScheduleExactAlarms()) {
                            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                Uri.parse("package:$packageName"))
                            startActivity(intent)
                        }
                    } catch (_: Exception) { }
                }
            }

            LaunchedEffect(Unit) {
                DatabaseActions.driverFactory = { DatabaseDriverFactory(applicationContext).createDriver() }
                try {
                    withContext(Dispatchers.IO) {
                        val driver = DatabaseDriverFactory(applicationContext).createDriver()
                        DatabaseActions.init(driver)
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
                    val equipoViewModelFactory = remember { EquipoViewModelFactory(applicationContext) }
                    val miPerfilViewModelFactory = remember { MiPerfilViewModelFactory(applicationContext) }
                    val tablerosViewModelFactory = remember { TablerosViewModelFactory(applicationContext) }
                    val leaderboardViewModelFactory = remember { LeaderboardViewModelFactory(applicationContext) }
                    val ajustesViewModelFactory = remember { AjustesViewModelFactory(applicationContext) }
                    val formularioViewModelFactory = remember { FormularioViewModelFactory(applicationContext) }
                    val operacionesPendientesViewModelFactory = remember { OperacionesPendientesViewModelFactory(applicationContext) }
                    val loginViewModelFactory = remember { LoginViewModelFactory(applicationContext) }
                    val networkObserver = remember { NetworkObserver(applicationContext) }

                    App(
                        tareaFactory,
                        equipoViewModelFactory,
                        miPerfilViewModelFactory,
                        tablerosViewModelFactory,
                        leaderboardViewModelFactory,
                        ajustesViewModelFactory,
                        formularioViewModelFactory,
                        operacionesPendientesViewModelFactory,
                        loginViewModelFactory,
                        networkObserver
                    )
                }
            }
        }
    }
}
