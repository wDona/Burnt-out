package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import dev.wdona.burntout.presentation.viewmodel.viewmodels.OperacionesPendientesViewModel

expect class OperacionesPendientesViewModelFactory {
    fun create(): OperacionesPendientesViewModel
}
