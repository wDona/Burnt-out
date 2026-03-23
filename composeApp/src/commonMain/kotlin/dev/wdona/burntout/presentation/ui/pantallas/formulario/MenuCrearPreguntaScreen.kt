package dev.wdona.burntout.presentation.ui.pantallas.formulario

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import dev.wdona.burntout.presentation.viewmodel.viewmodelfactories.FormularioViewModelFactory

@Deprecated("En principio solo se van a usar preguntas probadas clinicamente")
class MenuCrearPreguntaScreen(private val viewModelFactory: FormularioViewModelFactory, private val onVolver: () -> Unit) : Screen {
    @Composable
    override fun Content() {
        TODO("Not yet implemented")
    }

}