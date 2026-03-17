package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import dev.wdona.burntout.presentation.viewmodel.viewmodels.FormularioViewModel

expect class FormularioViewModelFactory {
    fun create(): FormularioViewModel
}

