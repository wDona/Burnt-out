package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import dev.wdona.burntout.presentation.viewmodel.viewmodels.FormularioViewModel
import dev.wdona.burntout.presentation.viewmodel.viewmodels.PreguntasInicialesViewModel

expect class FormularioViewModelFactory {
    fun createFormularioViewModel(): FormularioViewModel
    fun createPreguntasInicialesViewModel(): PreguntasInicialesViewModel
}

