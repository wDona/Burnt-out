package dev.wdona.burntout.presentation.viewmodel.viewmodelfactories

import dev.wdona.burntout.presentation.viewmodel.viewmodels.LeaderboardViewModel

expect class LeaderboardViewModelFactory {
    fun create(): LeaderboardViewModel
}
