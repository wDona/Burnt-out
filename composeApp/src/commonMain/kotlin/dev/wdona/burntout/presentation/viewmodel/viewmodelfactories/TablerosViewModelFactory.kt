package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import dev.wdona.burntout.presentation.viewmodel.viewmodels.TablerosViewModel

expect class TablerosViewModelFactory {
    fun create(): TablerosViewModel
}
