package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import dev.wdona.burntout.presentation.viewmodel.viewmodels.LoginViewModel

expect class LoginViewModelFactory {
    fun create(): LoginViewModel
}
