package dev.wdona.burntout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import burnt_out.composeapp.generated.resources.Res
import burnt_out.composeapp.generated.resources.logoBurntOutIcon
import dev.wdona.burntout.presentation.ui.theme.BurntOutMaterialTheme
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.*
import dev.wdona.burntout.shared.db.DatabaseDriverFactory
import dev.wdona.burntout.shared.db.DatabaseInit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    var isDatabaseReady by remember { mutableStateOf(false) }

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
        }
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Burn't out",
        icon = painterResource(Res.drawable.logoBurntOutIcon) // FIXME NO FUNCINOA
    ) {
        BurntOutMaterialTheme {
            Surface (
                modifier = Modifier.fillMaxSize(),
                color = BurntOutMaterialTheme.getColorScheme().background
            ){
                if (isDatabaseReady) {
                    App(
                        tareaFactory = TareasViewModelFactory(),
                        miEquipoViewModelFactory = MiEquipoViewModelFactory(),
                        miPerfilViewModelFactory = MiPerfilViewModelFactory(),
                        tablerosViewModelFactory = TablerosViewModelFactory(),
                        leaderboardViewModelFactory = LeaderboardViewModelFactory(),
                        ajustesViewModelFactory = AjustesViewModelFactory()
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
