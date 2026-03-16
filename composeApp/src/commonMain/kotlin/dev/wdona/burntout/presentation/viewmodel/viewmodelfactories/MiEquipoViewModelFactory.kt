package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import dev.wdona.burntout.presentation.viewmodel.viewmodels.EquipoViewModel

expect class MiEquipoViewModelFactory {
    fun create(): EquipoViewModel
}