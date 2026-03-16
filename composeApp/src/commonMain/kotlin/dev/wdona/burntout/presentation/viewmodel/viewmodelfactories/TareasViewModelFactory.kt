package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import dev.wdona.burntout.presentation.viewmodel.viewmodels.TareasViewModel

expect class TareasViewModelFactory {
    fun create(): TareasViewModel
}