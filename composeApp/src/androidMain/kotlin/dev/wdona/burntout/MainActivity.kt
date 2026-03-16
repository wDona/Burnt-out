package dev.wdona.burntout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.AjustesViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.MiEquipoViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.LeaderboardViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.MiPerfilViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.TablerosViewModelFactory
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.TareasViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val tareaFactory = remember { TareasViewModelFactory(applicationContext) }
            val miEquipoViewModelFactory = remember { MiEquipoViewModelFactory(applicationContext) }
            val miPerfilViewModelFactory = remember { MiPerfilViewModelFactory(applicationContext) }
            val tablerosViewModelFactory = remember { TablerosViewModelFactory(applicationContext) }
            val leaderboardViewModelFactory = remember { LeaderboardViewModelFactory(applicationContext) }
            val ajustesViewModelFactory = remember { AjustesViewModelFactory(applicationContext) }


            App(
                tareaFactory,
                miEquipoViewModelFactory,
                miPerfilViewModelFactory,
                tablerosViewModelFactory,
                leaderboardViewModelFactory,
                ajustesViewModelFactory
            )
        }
    }
}
