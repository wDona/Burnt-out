package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import dev.wdona.burntout.presentation.viewmodel.viewmodels.PerfilViewModel

expect class MiPerfilViewModelFactory {
    fun create(): PerfilViewModel
}